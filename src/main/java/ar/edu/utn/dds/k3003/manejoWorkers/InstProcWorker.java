package ar.edu.utn.dds.k3003.manejoWorkers;

import lombok.Setter;

public class InstProcWorker {
    private final ProcesadorWorker worker;
    private boolean ocupado;
    @Setter
    private boolean aBorrar;

    public InstProcWorker(ProcesadorWorker worker) {
        this.worker = worker;
        this.ocupado = false;
        this.aBorrar = false;
    }

    public ProcesadorWorker worker() {
        return worker;
    }

    public boolean ocupado() {
        return ocupado;
    }

    public synchronized void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String getUrl() {
        return worker.getBaseURL();
    }
    public boolean aBorrar() {return aBorrar;}
}
