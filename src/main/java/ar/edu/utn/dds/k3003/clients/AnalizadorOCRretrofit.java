package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;
public interface AnalizadorOCRretrofit {
    @POST("api.ocr.space/parse/image")
    Call<List<OCRspaceDTO>> analizarImagenOCR(@Query("image") String image);

}
