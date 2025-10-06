package ar.edu.utn.dds.k3003.analizadores;
import ar.edu.utn.dds.k3003.model.PdI;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class Procesador {
    AnalizadorOCR analizadorOCR = new AnalizadorOCRSpace();
    Etiquetador etiquetador = new EtiquetadorAPILayer();

    public void procesar(PdI pdi) {
        String urlImagen = pdi.getUrlImagen();

        CompletableFuture<String> tareaOCR = analizarConOCR(urlImagen);
        CompletableFuture<List<String>> tareaEtiquetas = obtenerEtiquetas(urlImagen);

        try {
            String contenidoOCR = tareaOCR.get();
            List<String> etiquetas = tareaEtiquetas.get();

            pdi.setTextoImagen(contenidoOCR);
            pdi.setEtiquetas(etiquetas);

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error en procesamiento asíncrono, Error: " + e, e);
        }
    }

    @Async("taskExecutor")  // Ejecuta en hilo separado
    public CompletableFuture<String> analizarConOCR(String urlImagen) {
        String resultado = analizadorOCR.analizarImagenURL(urlImagen);
        return CompletableFuture.completedFuture(resultado);
    }

    @Async("taskExecutor")  // Ejecuta en hilo separado
    public CompletableFuture<List<String>> obtenerEtiquetas(String urlImagen) {
        List<String> etiquetas = etiquetador.obtenerEtiquetas(urlImagen);
        return CompletableFuture.completedFuture(etiquetas);
    }
}
