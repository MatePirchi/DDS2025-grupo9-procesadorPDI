package ar.edu.utn.dds.k3003.manejoWorkers;
import ar.edu.utn.dds.k3003.clients.ProcesadorWorkerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.model.PdI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ProcesadorMaster {
    private final List<InstProcWorker> workers;
    private final List<PDIDTO> queue; // Cambiado a ArrayList
    private final ExecutorService executorService;
    private final ExecutorService procesadorColaExecutor;
    private final Semaphore workersSemaphore; // Semáforo para controlar workers disponibles
    private final Semaphore queueSemaphore; // Semáforo para controlar items en cola
    private volatile boolean running;

    public ProcesadorMaster() {
        this.workers = new ArrayList<>();
        this.queue = new ArrayList<>(); // Inicializar como ArrayList
        this.executorService = Executors.newCachedThreadPool();
        this.procesadorColaExecutor = Executors.newSingleThreadExecutor();
        this.workersSemaphore = new Semaphore(0); // Inicialmente sin permisos
        this.queueSemaphore = new Semaphore(0); // Inicialmente sin items en cola
        this.running = true;
        
        // Iniciar el procesador de cola en un hilo dedicado
        this.procesadorColaExecutor.submit(this::procesarColaConSemaforo);
    }

    public void agregarWorker(String baseURL){
        workers.add(new InstProcWorker(new ProcesadorWorkerProxy(new ObjectMapper(), baseURL)));
        workersSemaphore.release(); // Incrementar el contador de workers disponibles
        System.out.println("Worker agregado: "+ baseURL + " (Total disponibles: " + workersSemaphore.availablePermits() + ")");
    }

    public void mandar_a_procesar_pdi(PdI pdi){
        PDIDTO pdiDTO = convertirADTO(pdi);
        
        // Intentar adquirir un worker sin bloquear (tryAcquire)
        if (workersSemaphore.tryAcquire()) {
            Optional<InstProcWorker> workerDisponible = encontrarWorkerDisponible();
            if (workerDisponible.isPresent()) {
                return;
            } else {
                System.out.println("Se paso el acquire de worker, sin embargo no se encontro worker libre");
                workersSemaphore.release();
            }
        }
        
        // Si no hay workers disponibles, encolar
        System.out.println("No hay workers disponibles. Encolando PDI: " + pdiDTO.id());
        synchronized (queue) {
            queue.add(pdiDTO);
            queueSemaphore.release(); // Señalizar que hay un item en cola
        }
    }

    private synchronized Optional<InstProcWorker> encontrarWorkerDisponible() {
        return workers.stream()
                .filter(w -> !w.ocupado())
                .findFirst();
    }

    private void procesarConWorker(InstProcWorker worker, PDIDTO pdiDTO) {
        worker.setOcupado(true);
        
        CompletableFuture.supplyAsync(() -> {
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

    private void procesarColaConSemaforo() {
        PDIDTO pdiDTO;
        while (running) {
            try {
                queueSemaphore.acquire();

                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        pdiDTO = queue.remove(0); // Extraer el primero
                    } else {
                        // No debería pasar, pero liberamos el semáforo por seguridad
                        continue;
                    }
                }

                System.out.println("PDI en cola detectado: " + pdiDTO.id() + ". Esperando worker disponible...");

                // Esperar hasta que haya un worker disponible (bloquea si no hay)
                workersSemaphore.acquire();

                // Ahora hay un worker disponible garantizado
                Optional<InstProcWorker> workerDisponible = encontrarWorkerDisponible();

                if (workerDisponible.isPresent()) {
                    if(workerDisponible.get().aBorrar()) { //Si el worker esta marcado como a borrar, se borra y se busca otro
                        workers.remove(workerDisponible.get());
                        synchronized (queue) {
                            queue.add(pdiDTO);
                        }
                        queueSemaphore.release();
                        continue;
                    }
                    System.out.println("Procesando PDI de la cola: " + pdiDTO.id());
                    procesarConWorker(workerDisponible.get(), pdiDTO);
                } else {
                    // No debería pasar, pero liberamos el semáforo y re-encolamos
                    System.out.println("Se paso el acquire de worker, sin embargo no se encontro worker libre");
                    workersSemaphore.release();
                    synchronized (queue) {
                        queue.add(pdiDTO);
                        queueSemaphore.release();
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
    public synchronized void borrarWorker(String url) {
        // Buscar el worker por URL
        Optional<InstProcWorker> workerAEliminar = workers.stream()
                .filter(w -> w.getUrl().equals(url))
                .findFirst();
        if (workerAEliminar.isPresent()) {
            InstProcWorker worker = workerAEliminar.get();
            worker.setABorrar(true);

            // Verificar si el worker está ocupado
            if (worker.ocupado()) {
                return;
            }
            workers.remove(worker);
            if (workersSemaphore.tryAcquire()) {
                System.out.println("Worker eliminado: " + url + " (Total disponibles: " + workersSemaphore.availablePermits() + ")");
            } else {
                System.out.println("Worker eliminado: " + url + " (pero semáforo ya estaba en 0)");
            }
            return;
        }
        System.out.println("Worker no encontrado: " + url);
        throw new NoSuchElementException("Worker no encontrado: " + url);
    }
}
