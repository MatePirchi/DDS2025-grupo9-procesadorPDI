package ar.edu.utn.dds.k3003.analizadores;

public record InstProcWorker(ProcesadorWorker worker, boolean ocupado) {
    public InstProcWorker(ProcesadorWorker worker) {
        this(worker, false);
    }
}
