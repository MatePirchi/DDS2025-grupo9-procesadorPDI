package ar.edu.utn.dds.k3003.analizadores;

import java.util.List;

public interface Etiquetador {
    List<String> obtenerEtiquetas(String urlImagen); //Retorna todas las etiquetas
}
