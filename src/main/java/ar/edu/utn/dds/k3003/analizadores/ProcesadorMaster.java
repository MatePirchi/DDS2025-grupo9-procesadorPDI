package ar.edu.utn.dds.k3003.analizadores;
import ar.edu.utn.dds.k3003.clients.ProcesadorWorkerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.model.PdI;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;


public class ProcesadorMaster {
    private final List<InstProcWorker> workers;
    private final BlockingQueue<PDIDTO> queue;
    private final ExecutorService executorService;
    private final ExecutorService procesadorColaExecutor;
    private final Semaphore workersSemaphore; // Semáforo para controlar workers disponibles
    private volatile boolean running;

    public ProcesadorMaster() {
        this.workers = new ArrayList<>();
        this.queue = new LinkedBlockingQueue<>();
        this.executorService = Executors.newCachedThreadPool();
        this.procesadorColaExecutor = Executors.newSingleThreadExecutor();
        this.workersSemaphore = new Semaphore(0); // Inicialmente sin permisos
        this.running = true;
        
        // Iniciar el procesador de cola en un hilo dedicado
        this.procesadorColaExecutor.submit(this::procesarColaConSemaforo);
    }

    public void agregarWorker(String baseURL){
        workers.add(new InstProcWorker(new ProcesadorWorkerProxy(new ObjectMapper(), baseURL)));
        workersSemaphore.release(); // Incrementar el contador de workers disponibles
        System.out.println("Worker agregado: "+ baseURL + " (Total disponibles: " + workersSemaphore.availablePermits() + ")");
    }

    public CompletableFuture<PDIDTO> mandar_a_procesar_pdi(PdI pdi){
        PDIDTO pdiDTO = convertirADTO(pdi);
        
        // Intentar adquirir un worker sin bloquear (tryAcquire)
        if (workersSemaphore.tryAcquire()) {
            // Hay un worker disponible
            Optional<InstProcWorker> workerDisponible = encontrarWorkerDisponible();
            if (workerDisponible.isPresent()) {
                return procesarConWorker(workerDisponible.get(), pdiDTO);
            } else {
                // No debería pasar, pero por seguridad liberamos el semáforo
                workersSemaphore.release();
            }
        }
        
        // Si no hay workers disponibles, encolar
        System.out.println("No hay workers disponibles. Encolando PDI: " + pdiDTO.id());
        if(!queue.offer(pdiDTO)){
            throw new RuntimeException("No se pudo agregar pdi a cola de procesamiento");
        }
        return CompletableFuture.completedFuture(pdiDTO);
    }

    private synchronized Optional<InstProcWorker> encontrarWorkerDisponible() {
        return workers.stream()
                .filter(w -> !w.ocupado())
                .findFirst();
    }

    private CompletableFuture<PDIDTO> procesarConWorker(InstProcWorker worker, PDIDTO pdiDTO) {
        worker.setOcupado(true);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Procesando PDI " + pdiDTO.id() + " con worker");
                PDIDTO resultado = worker.worker().procesar(pdiDTO);
                System.out.println("PDI "+pdiDTO.id()+" procesado exitosamente");
                return resultado;
            } catch (Exception e) {
                System.out.println("Error procesando PDI: " +pdiDTO.id()+ ", error: "+ e.getMessage());
                throw new CompletionException(e);
            } finally {
                worker.setOcupado(false);
                workersSemaphore.release(); // Liberar el semáforo cuando el worker termina
                System.out.println("Worker liberado (Disponibles: " + workersSemaphore.availablePermits() + ")");
            }
        }, executorService);
    }

    /**
     * Procesa PDIs de la cola usando semáforo para sincronización.
     * El semáforo bloquea automáticamente cuando no hay workers disponibles.
     */
    private void procesarColaConSemaforo() {
        while (running) {
            try {
                // Esperar hasta 500ms por un PDI de la cola
                PDIDTO pdiDTO = queue.poll(500, TimeUnit.MILLISECONDS);
                
                if (pdiDTO != null) {
                    System.out.println("PDI en cola detectado: " + pdiDTO.id() + ". Esperando worker disponible...");
                    
                    // Esperar hasta que haya un worker disponible (bloquea si no hay)
                    workersSemaphore.acquire();
                    
                    // Ahora hay un worker disponible garantizado
                    Optional<InstProcWorker> workerDisponible = encontrarWorkerDisponible();
                    
                    if (workerDisponible.isPresent()) {
                        System.out.println("Procesando PDI de la cola: " + pdiDTO.id());
                        procesarConWorker(workerDisponible.get(), pdiDTO);
                    } else {
                        // No debería pasar, pero liberamos el semáforo por seguridad
                        System.out.println("Se paso el acquire de worker, sin embargo no se encontro worker libre");
                        workersSemaphore.release();
                        if (queue.offer(pdiDTO)){
                            throw new RuntimeException("No se pudo agregar pdi a cola de procesamiento");
                        }


                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Procesador de cola interrumpido");
                break;
            }
        }
    }

    public PDIDTO convertirADTO(PdI pdi) {
        return new PDIDTO(
                String.valueOf(pdi.getId()),
                pdi.getHechoId(),
                pdi.getDescripcion(),
                pdi.getLugar(),
                pdi.getMomento(),
                pdi.getUrlImagen(),
                pdi.getTextoImagen(),
                pdi.getEtiquetas());
    }

    public void shutdown() {
        System.out.println("Iniciando shutdown del ProcesadorMaster");
        running = false;
        procesadorColaExecutor.shutdown();
        executorService.shutdown();
        
        try {
            if (!procesadorColaExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                procesadorColaExecutor.shutdownNow();
            }
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            procesadorColaExecutor.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
