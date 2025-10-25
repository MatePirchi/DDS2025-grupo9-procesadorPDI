package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ApiLayerException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

public class EtiquetadorAPILayerProxy {
    private final EtiquetadorAPILayerRetrofitClient service;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public EtiquetadorAPILayerProxy(ObjectMapper objectMapper, String apiKey) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.apilayer.com/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(EtiquetadorAPILayerRetrofitClient.class);

    }

    public List<EtiquetadorAPILayerDTO> obtenerEtiquetas(String urlImagen) {
        try {
            var response = service.getImageLabeling(this.apiKey, urlImagen).execute();
            if (!response.isSuccessful()) {
                // Check error body for "no labels found" message
                String errorMessage = "";
                if (response.errorBody() != null) {
                    System.out.println("Error Body no es null " + response.errorBody().string() + " " + response.errorBody());

                    try {
                        JsonNode errorJson = objectMapper.readTree(response.errorBody().byteStream());
                        if (errorJson.has("message")) {
                            errorMessage = errorJson.get("message").asText();
                        } else {
                            System.out.println("Error en la obtenerEtiquetas: " + errorJson);
                        }
                    } catch (Exception e) {
                        errorMessage = "Could not read error message";
                    }
                }

                if (errorMessage.toLowerCase().contains("no labels found")) {
                    return List.of(); // Return empty list when no labels found
                }
                
                throw new ApiLayerException("Respuesta de ApiLayer no Exitosa: " + response.message() + " - " + errorMessage);
            }
            if (response.body() == null) {
                throw new ApiLayerException("Cuerpo de Respuesta de ApiLayer vacio");
            }
            return response.body();
        }
        catch (Exception e) {
            throw new ApiLayerException("Fallo en la comunicacion con APILayer:" + e.getMessage());
        }
    }
}