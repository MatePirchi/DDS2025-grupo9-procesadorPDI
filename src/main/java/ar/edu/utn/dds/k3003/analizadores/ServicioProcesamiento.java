package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.model.PdI;

public interface ServicioProcesamiento {
    /**
     * Procesa una imagen y realiza los cambios a pdi que debe hacer
     * Retorna true si no hubo error, false si lo hubo (no se guarda nada)
     */
    Boolean procesar(String urlImagen, PdI pdi);
}
