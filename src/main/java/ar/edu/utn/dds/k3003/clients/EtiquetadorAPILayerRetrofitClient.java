package ar.edu.utn.dds.k3003.clients;
import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface EtiquetadorAPILayerRetrofitClient {
    @GET ("image_labeling/url")
    Call<List<EtiquetadorAPILayerDTO>> getImageLabeling(@Header("apikey") String apikey,@Query("url")String urlImagen);

}
