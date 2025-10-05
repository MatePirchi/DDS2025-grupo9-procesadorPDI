package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaFallidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

public class EtiquetadorAPILayerProxy {
    private final EtiquetadorAPILayerRetrofitClient service;

    public EtiquetadorAPILayerProxy(ObjectMapper objectMapper) {
        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.apilayer.com/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(EtiquetadorAPILayerRetrofitClient.class);

    }

    public List<EtiquetadorAPILayerDTO> obtenerEtiquetas(String urlImagen) {
        try {
            var response = service.getImageLabeling("ZUfYsPoInCZSDCdow7BH5El4IPAzmlBm", urlImagen).execute();
            if (!response.isSuccessful()) {throw new ComunicacionExternaFallidaException("Respuesta de ApiLayer no Exitosa");}
            if (response.body() == null) {throw new ComunicacionExternaFallidaException("Cuerpo de Respuesta de ApiLayer vacio");}
            return response.body();
        }
        catch (Exception e) {
            throw new ComunicacionExternaFallidaException("Fallo en la comunicacion con APILayer:" + e.getMessage());
        }
    }
}