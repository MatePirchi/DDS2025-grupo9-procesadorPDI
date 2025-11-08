package ar.edu.utn.dds.k3003.analizadores;
import ar.edu.utn.dds.k3003.clients.ProcesadorWorkerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.model.PdI;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ProcesadorMaster {
    private List<InstProcWorker> workers;
    private BlockingQueue<PDIDTO> queue = new LinkedBlockingQueue<>();


    public void agregarWorker(String baseURL){
        workers.add(new InstProcWorker(new ProcesadorWorkerProxy( new ObjectMapper(), baseURL)));
    }


}
