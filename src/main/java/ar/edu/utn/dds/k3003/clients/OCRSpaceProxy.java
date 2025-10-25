package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaException;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.OCRspaceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.ConnectException;

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
            if (!response.isSuccessful()) {throw new ConnectException("Respuesta no Exitosa");}
            if (response.body() == null) { throw new ConnectException("Cuerpo de Respuesta vacio"); }

            return response.body();
        }
        catch ( Exception e){
            throw new OCRspaceException("Fallo en la comunicacion con OCRSpace: " + e.getMessage());
        }

    }
}
