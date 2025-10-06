package ar.edu.utn.dds.k3003.clients.dtos;

import java.time.LocalDateTime;
import java.util.List;

public record PDIDTO(String id, String hechoId, String descripcion, String lugar, LocalDateTime momento, String urlImagen, String textoImagen, List<String> etiquetas) {
    public PDIDTO(String id, String hechoId) {
        this(id, hechoId, null, null, null, null, null, List.of());
    }

}