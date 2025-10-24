package ar.edu.utn.dds.k3003.analizadores;

import java.util.List;

public interface ServicioProcesamiento {
    /**
     * Procesa una imagen y retorna los resultados como lista de strings.
     * Para OCR: retorna una lista con el texto extraído.
     * Para Etiquetador: retorna una lista con las etiquetas detectadas.
     */
    List<String> procesar(String urlImagen);
}
