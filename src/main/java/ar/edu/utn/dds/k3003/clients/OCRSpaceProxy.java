package ar.edu.utn.dds.k3003.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class OCRSpaceProxy implements  AnalizadorOCR {
    private final OCRSpaceRetrofitClient service;

    public OCRSpaceProxy(ObjectMapper objectMapper) {
        var env = System.getenv();

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.ocr.space/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(OCRSpaceRetrofitClient.class);

    }

    @Override
    public String analizarImagenURL(String imagenURL) {
        try {
            var response = service.analizarImagenOCR("K89669199988957","eng", false, imagenURL, false, false).execute();
            if (!response.isSuccessful()) {throw new RuntimeException("Respuesta no Exitosa");}
            if (response.body() == null) { throw new RuntimeException("Cuerpo de Respuesta vacio"); }

            if(response.body().ParsedResults().size() > 1)
                System.out.println("WARNING: OCRSpaceProxy recibio el analisis de mas de 1 imagen, pero se envio solo 1 imagen ");

            if(response.body().ParsedResults().get(0).ParsedText().isEmpty())
                return "";

            assert response.body().ParsedResults().get(0).TextOverlay()!= null;
            if(!response.body().ParsedResults().get(0).TextOverlay().Lines().isEmpty())
                System.out.println("WARNING: OCRSpaceProxy recibio el Text Overlay de alguna imagen");

            return response.body().ParsedResults().get(0).ParsedText();

        }
        catch ( Exception e){
            throw new RuntimeException("Fallo en la comunicacion con OCRSpace:" + e.getMessage());
        }

    }
}
