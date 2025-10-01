package ar.edu.utn.dds.k3003.clients;


import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface SolicitudesRetrofitClient {

    @GET("solicitudes")
    Call<List<SolicitudDTO>> getSolicitudes(@Query("hecho") String hechoId);
}
