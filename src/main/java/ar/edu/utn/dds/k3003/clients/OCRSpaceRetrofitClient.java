package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
public interface OCRSpaceRetrofitClient {

    @FormUrlEncoded
    @POST("parse/image")//(eng, false, <url>, false, false) Usar esp si se quiere que busque en español
    Call<OCRspaceDTO> analizarImagenOCR(@Header("apikey")String apikey, @Field("language") String lang, @Field("isOverlayRequired") boolean isOverlayRequired,
                                        @Field("url") String url, @Field("iscreatesearchablepdf") boolean iscreatesearchablepdf,
                                        @Field("issearchablepdfhidetextlayer") boolean issearchablepdfhidetextlayer);

}
