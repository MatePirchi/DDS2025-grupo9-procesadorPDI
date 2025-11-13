package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProcesadorWorkerClient {
    @POST("api/")
    Call<ResponseBody> mandar_pdi_a_worker(@Body PDIDTO pdiDTO);
}
