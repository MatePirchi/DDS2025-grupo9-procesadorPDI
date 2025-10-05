package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaFallidaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class OCRSpaceProxy  {
    private final OCRSpaceRetrofitClient service;

    public OCRSpaceProxy(ObjectMapper objectMapper) {

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://api.ocr.space/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(OCRSpaceRetrofitClient.class);

    }

    public OCRspaceDTO hacerPedidoAOCRSpace(String imagenURL) {
        try {
            var response = service.analizarImagenOCR("K89669199988957","eng", false, imagenURL, false, false).execute();
            if (!response.isSuccessful()) {throw new ComunicacionExternaFallidaException("Respuesta de OCRSpace no Exitosa");}
            if (response.body() == null) { throw new ComunicacionExternaFallidaException("Cuerpo de Respuesta de OCRSpace vacio"); }

            return response.body();
        }
        catch ( Exception e){
            throw new ComunicacionExternaFallidaException("Fallo en la comunicacion con OCRSpace:" + e.getMessage());
        }

    }
}
