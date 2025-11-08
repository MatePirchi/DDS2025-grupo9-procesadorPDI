package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.analizadores.ProcesadorWorker;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

public class ProcesadorWorkerProxy implements ProcesadorWorker {

    private final ProcesadorWorkerClient service;

    public ProcesadorWorkerProxy(ObjectMapper objectMapper, String baseURL) {

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl(baseURL)
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(ProcesadorWorkerClient.class);
    }

    @Override
    public PDIDTO procesar(PDIDTO pdidto) {
        return null;
    }
}
