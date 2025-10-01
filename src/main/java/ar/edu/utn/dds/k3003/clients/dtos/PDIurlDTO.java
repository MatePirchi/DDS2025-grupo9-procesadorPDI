package ar.edu.utn.dds.k3003.clients.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PDIurlDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String urlImagen, String resultadoOCR, List<String> etiquetas) {
    public PDIurlDTO(String id, String hechoId) {
        this(id, hechoId, (String)null, (String)null, (LocalDateTime)null, (String)null, (String)null, List.of());
    }

}