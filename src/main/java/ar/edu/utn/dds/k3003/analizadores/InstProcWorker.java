package ar.edu.utn.dds.k3003.analizadores;

public class InstProcWorker {
    private final ProcesadorWorker worker;
    private boolean ocupado;

    public InstProcWorker(ProcesadorWorker worker) {
        this.worker = worker;
        this.ocupado = false;
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
}
