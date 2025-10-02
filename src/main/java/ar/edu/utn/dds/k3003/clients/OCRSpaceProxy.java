package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;

public class OCRSpaceProxy implements  AnalizadorOCR {
    private final OCRSpaceRetrofitClient service;

    public OCRSpaceProxy(ObjectMapper objectMapper) {
        var env = System.getenv();

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://two025-tp-entrega-2-francoquiroga01.onrender.com/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(OCRSpaceRetrofitClient.class);

    }

    @Override
    public String analizarImagenURL(String imagenURL) {
        OCRspaceDTO rta;
        try {
            var response = service.analizarImagenOCR(imagenURL).execute();
            if (!response.isSuccessful() && response.body() == null) {
                throw new RuntimeException("Error al analizar la imagen con OCRSpace");
            }

            assert response.body() != null;
            rta  = response.body();

            if(rta.parsedResults().size() > 1)
                System.out.println("WARNING: OCRSpaceProxy recibio el analisis de mas de 1 imagen, pero se envio solo 1 imagen ");

            if(rta.parsedResults().getFirst().parsedText().isEmpty())
                return "";

            assert rta.parsedResults().getFirst().txtOverlays()!= null;
            if(!rta.parsedResults().getFirst().txtOverlays().isEmpty())
                System.out.println("WARNING: OCRSpaceProxy recibio el Text Overlay de alguna imagen");
        }
        catch ( Exception e){
            throw new RuntimeException("Fallo en la comunicacion con OCRSpace", e);
        }

        return rta.parsedResults().getFirst().parsedText();
    }
}
