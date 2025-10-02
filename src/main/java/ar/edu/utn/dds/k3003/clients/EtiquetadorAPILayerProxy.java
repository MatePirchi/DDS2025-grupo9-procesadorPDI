package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.ArrayList;
import java.util.List;

public class EtiquetadorAPILayerProxy implements Etiquetador{
    private final EtiquetadorAPILayerRetrofitClient service;

    public EtiquetadorAPILayerProxy(ObjectMapper objectMapper) {
        var env = System.getenv();

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.apilayer.com/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(EtiquetadorAPILayerRetrofitClient.class);

    }


    @Override
    public List<String> obtenerEtiquetas(String urlImagen) {
        try {
            var response = service.getImageLabeling("ZUfYsPoInCZSDCdow7BH5El4IPAzmlBm", urlImagen).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("Respuesta no Exitosa");
            }
            if (response.body() == null) {
                throw new RuntimeException("Cuerpo de Respuesta vacio");
            }
            List<String> etiquetas = new ArrayList<>();
            response.body().stream()
                    .filter( e-> e.confidence()> 0.60)
                    .forEach(e -> etiquetas.add(e.label()));

            return etiquetas;
        }
        catch (Exception e) {
            throw new RuntimeException("Fallo en la comunicacion con APILayer:" + e.getMessage());
        }
    }
}