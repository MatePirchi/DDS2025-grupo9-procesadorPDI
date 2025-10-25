package ar.edu.utn.dds.k3003.analizadores;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ApiLayerException;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.OCRspaceException;
import ar.edu.utn.dds.k3003.model.PdI;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class Procesador {
    private final List<ServicioProcesamiento> servicios = List.of(
            new AnalizadorOCRSpace(), new EtiquetadorAPILayer()
    );


    public void procesar(PdI pdi) {
        String urlImagen = pdi.getUrlImagen();
        System.out.println("Procesando imagen en url: " + urlImagen);
        List<CompletableFuture<List<String>>> tareas = new ArrayList<>();
        //Defino todas las tareas a hacer
        servicios.forEach(servicio -> tareas.add(realizarProceso(urlImagen, servicio)));

        try {
            String contenidoOCR = tareas.get(0).get().get(0); //Solamente retorna 1 String, que es el texto
            List<String> etiquetas = tareas.get(1).get();

            pdi.setTextoImagen(contenidoOCR);
            pdi.setEtiquetas(etiquetas);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error en procesamiento asíncrono, Error: " + e, e);
        }

    }

    @Async("taskExecutor")  // Ejecuta en hilo separado
    public CompletableFuture<List<String>> realizarProceso(String urlImagen, ServicioProcesamiento servicio) {
        try {
            List<String> resultado = servicio.procesar(urlImagen);
            return CompletableFuture.completedFuture(resultado);

        } catch (OCRspaceException e){
            System.out.println("Error con OCRspace con url: " + urlImagen);
            return CompletableFuture.completedFuture(List.of(""));
        }catch (ApiLayerException e){
            System.out.println("Error con ApiLayer con url: " + urlImagen);
            return CompletableFuture.completedFuture(List.of());
        }
    }
}
/*


 */