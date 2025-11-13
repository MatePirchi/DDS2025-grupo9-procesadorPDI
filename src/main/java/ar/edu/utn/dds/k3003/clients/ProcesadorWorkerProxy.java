package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.manejoWorkers.ProcesadorWorker;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.ConnectException;

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
        try {
            var response = service.mandar_pdi_a_worker(pdidto).execute();
            if (!response.isSuccessful()) {throw new ConnectException("Respuesta no Exitosa");}
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
