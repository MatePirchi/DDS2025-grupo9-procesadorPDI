package ar.edu.utn.dds.k3003.analizadores;

public interface AnalizadorOCR extends ServicioProcesamiento {
    String analizarImagenURL(String imagenURL); //Retorna el Texto Leido
}
