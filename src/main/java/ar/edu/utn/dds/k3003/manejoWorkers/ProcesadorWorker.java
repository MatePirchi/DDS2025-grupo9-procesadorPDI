package ar.edu.utn.dds.k3003.manejoWorkers;

import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;

public interface ProcesadorWorker {
    PDIDTO procesar(PDIDTO pdidto);
}
