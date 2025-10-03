package ar.edu.utn.dds.k3003.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ApplicationMetrics {
    private final MeterRegistry registry;
    private final Counter consultas;
    private final Counter pdisProcesados;
    private final Counter errores;
    private final Counter erroresAprobacion;
    private final Timer   tiempoProcesoPdi;

    public ApplicationMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.consultas          = Counter.builder("app.consultas.total")
                .description("Cantidad de consultas (lecturas)")
                .register(registry);
        this.pdisProcesados = Counter.builder("app.pdis.procesados")
                .description("Cantidad de pdis procesados")
                .register(registry);
        this.errores            = Counter.builder("app.errores.total")
                .description("Errores totales")
                .register(registry);
        this.erroresAprobacion  = Counter.builder("app.errores.aprobacion")
                .description("Errores de negocio de aprobación")
                .register(registry);
        this.tiempoProcesoPdi   = Timer.builder("app.pdi.proceso.latency")
                .description("Latencia procesar PdI")
                .publishPercentileHistogram()
                .register(registry);
    }

    public void incrementCounter(String name, String... tags) {
        Counter.Builder builder = Counter.builder(name);
        for (int i = 0; i < tags.length; i += 2) {
            builder.tag(tags[i], tags[i + 1]);
        }
        builder.register(registry).increment();
    }

    public void incConsulta() { 
        System.out.println("Incrementing consultas counter");
        incrementCounter("app.consultas.total");
    }
    public void incPdisProc() { 
        System.out.println("Incrementing pdisProcesados counter");
        pdisProcesados.increment(); 
    }
    public void incError() { 
        System.out.println("Incrementing errores counter");
        errores.increment(); 
    }
    public void incErrorAprobacion() { 
        System.out.println("Incrementing erroresAprobacion counter");
        erroresAprobacion.increment(); 
    }
    public <T> T timeProceso(java.util.concurrent.Callable<T> task) {
        try {
            return tiempoProcesoPdi.recordCallable(task);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
