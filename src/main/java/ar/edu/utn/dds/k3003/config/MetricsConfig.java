package ar.edu.utn.dds.k3003.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsConfig {
    private final MeterRegistry registry;
    public MetricsConfig(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementCounter(String name, String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException("Tags must be provided as key-value pairs. Number of tags must be even.");
        }
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
        incrementCounter("app.pdisproc.total");
    }
    public void incError() { 
        System.out.println("Incrementing errores counter");
        incrementCounter("app.errores.total");
    }
    public void incErrorAprobacion() { 
        System.out.println("Incrementing erroresAprobacion counter");
        incrementCounter("app.erroresAprobacion.total");
    }
    public void incErrorServicioExterno(){
        System.out.println("Incrementing errorServicioExterno counter");
        incrementCounter("app.errorServicioExterno.total");
    }
}
