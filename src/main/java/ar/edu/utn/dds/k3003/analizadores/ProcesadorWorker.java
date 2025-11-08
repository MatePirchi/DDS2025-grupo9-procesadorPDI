package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;

public interface ProcesadorWorker {
    public PDIDTO procesar(PDIDTO pdidto);
}
