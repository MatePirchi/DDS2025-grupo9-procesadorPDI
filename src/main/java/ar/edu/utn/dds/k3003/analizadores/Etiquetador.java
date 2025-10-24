package ar.edu.utn.dds.k3003.analizadores;

import java.util.List;

public interface Etiquetador extends ServicioProcesamiento {
    List<String> obtenerEtiquetas(String urlImagen); //Retorna todas las etiquetas
}
