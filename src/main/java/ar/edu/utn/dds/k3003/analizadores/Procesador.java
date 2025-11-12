package ar.edu.utn.dds.k3003.analizadores;
import ar.edu.utn.dds.k3003.config.MetricsConfig;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.PdIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class Procesador {
    private final List<ServicioProcesamiento> servicios;
    @Autowired
    private MetricsConfig metrics;

    public Procesador(AnalizadorOCRSpace analizadorOCRSpace, EtiquetadorAPILayer  etiquetadorAPILayer) {
        this.servicios = List.of(
                analizadorOCRSpace,
                etiquetadorAPILayer
        );
    }

    @Async("taskExecutor")
    public void procesar(PdI nuevoPdI, PdIRepository pdiRepository){

        if (!this.seDebeGuardar(nuevoPdI, pdiRepository)) {
            return;
        }


        if (nuevoPdI.getUrlImagen() != null &&(nuevoPdI.getTextoImagen() == null || nuevoPdI.getEtiquetas().isEmpty())) {
            this.iniciarWorkers(nuevoPdI);
            metrics.incPdisProc();
        }

        pdiRepository.save(nuevoPdI);
        System.out.println(
                "Se guardó el PdI con ID "
                        + nuevoPdI.getId()
                        + " en hechoId: "
                        + nuevoPdI.getHechoId());

    }

    public void iniciarWorkers(PdI pdi) {
        String urlImagen = pdi.getUrlImagen();
        System.out.println("Procesando imagen en url: " + urlImagen);
        List<CompletableFuture<Boolean>> tareas = new ArrayList<>();
        List<Boolean> resultados = new ArrayList<>();
        //Defino todas las tareas a hacer
        servicios.forEach(servicio -> tareas.add(realizarProceso(urlImagen, servicio, pdi)));

        try {
            for (CompletableFuture<Boolean> c : tareas) {
                resultados.add(c.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error en procesamiento asíncrono, Error: " + e, e);
        }
        int n = resultados.stream().filter(r -> !r).toList().size();
        if(n > 0){
            System.out.println("Ocurrio un error en "+ n + " sevicios, guardando los datos que se puedan");
        }

    }

    @Async("taskExecutor")  // Ejecuta en hilo separado
    public CompletableFuture<Boolean> realizarProceso(String urlImagen, ServicioProcesamiento servicio, PdI pdi) {
        Boolean resultado = servicio.procesar(urlImagen, pdi);
        return CompletableFuture.completedFuture(resultado);

    }

    private boolean seDebeGuardar(PdI nuevoPdI, PdIRepository pdiRepository) {
        Optional<PdI> yaProcesado;
        try{
            yaProcesado = pdiRepository.findById(nuevoPdI.getId());
            //Si el pdi está en la BDD puede que no haga falta hacer nada
            if(yaProcesado.isPresent() && (yaProcesado.get().getUrlImagen() != null && yaProcesado.get().getUrlImagen().equals(nuevoPdI.getUrlImagen()))){
                if(yaProcesado.get().getTextoImagen() == null || yaProcesado.get().getEtiquetas().isEmpty()){
                    //Si tiene URL, pero no tiene texto o etiquetas, se reintenta procesar. Por lo que habra que guardarlo de vuelta
                    return true;
                }
                //Si ya fue procesado la url, agrego el resultado al nuevo pdi
                nuevoPdI.setTextoImagen(yaProcesado.get().getTextoImagen());
                nuevoPdI.setEtiquetas(yaProcesado.get().getEtiquetas());

                //Si no cambio ningun dato del Pdi, no hace falta guardar nada
                return !yaProcesado.get().getHechoId().equals(nuevoPdI.getHechoId())
                        ||( yaProcesado.get().getDescripcion() != null ?
                        !yaProcesado.get().getDescripcion().equals(nuevoPdI.getDescripcion()) : nuevoPdI.getDescripcion() != null )

                        ||( yaProcesado.get().getLugar() != null?
                        !yaProcesado.get().getLugar().equals(nuevoPdI.getLugar()) : nuevoPdI.getLugar() != null )

                        ||( yaProcesado.get().getMomento() != null ?
                        !yaProcesado.get().getMomento().equals(nuevoPdI.getMomento()) : nuevoPdI.getMomento() != null );
                //Si el campo del pdi en memoria no es nulo, y es diferente del campo del pdi recibido, se debe guardar (retornar true)
                //Si el campo del pdi en memoria es nulo, solo se debe guardar si el campo del pdi recibido no es nulo
                //hechoId nunca debe ser nulo
            }
            //Si el pdi no esta en la BDD o hace falta procesar el nuevo url, entonces hay que guardarlo
            return true;
        }
        catch (NullPointerException e) {
            throw new RuntimeException("Algun campo de la entidad es nula: " + nuevoPdI.getId() + " " + nuevoPdI.getHechoId() + " " + nuevoPdI.getDescripcion()
                    + " " + nuevoPdI.getLugar()+ " " + nuevoPdI.getMomento() + " " + nuevoPdI.getUrlImagen() + "error: " + e, e);
        }
    }
}