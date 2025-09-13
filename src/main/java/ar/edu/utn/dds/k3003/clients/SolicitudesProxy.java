package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.facades.FachadaFuente;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoSolicitudBorradoEnum;
import ar.edu.utn.dds.k3003.facades.dtos.SolicitudDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.NoSuchElementException;

public class SolicitudesProxy implements FachadaSolicitudes {

    private final String endpoint;
    private final SolicitudesRetrofitClient service; // debe terminar en "/"

    public SolicitudesProxy(ObjectMapper objectMapper) {

        var env = System.getenv();
        this.endpoint = env.getOrDefault("URL_SOLICITUDES", "https://two025-tp-entrega-2-francoquiroga01.onrender.com");

        var retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://two025-tp-entrega-2-francoquiroga01.onrender.com/")
                        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                        .build();

        this.service = retrofit.create(SolicitudesRetrofitClient.class);
    }

    private record HechoResponseDTO(String hechoId, boolean activo) {}

    private String api(String path) {
        return service + (path.startsWith("/") ? path.substring(1) : path);
    }

    @Override
    public SolicitudDTO agregar(SolicitudDTO solicitudDTO) {
        return null;
    }

    @Override
    public SolicitudDTO modificar(String id, EstadoSolicitudBorradoEnum estado, String motivo) throws NoSuchElementException {
        return null;
    }

    @Override
    public List<SolicitudDTO> buscarSolicitudXHecho(String hechoId) {
        return null;
    }

    @Override
    public SolicitudDTO buscarSolicitudXId(String id) {
        return null;
    }

    @Override
    public boolean estaActivo(String hechoId) {
        try {
            var response = service.getSolicitudes(hechoId).execute();
            if(response.isSuccessful() && response.body() != null) {
                return !response.body().isEmpty(); //Si la lista que me retornan esta vacía asumo que no está activo
            }
            throw new RuntimeException( "Error al comprobar si hecho de ID: " + hechoId + " esta activo");
        }
        catch ( Exception e){
            throw new RuntimeException("Fallo en la communication con Solicitud", e);
        }
    }
    @Override
    public void setFachadaFuente(FachadaFuente fachadaFuente) {
    }
}
