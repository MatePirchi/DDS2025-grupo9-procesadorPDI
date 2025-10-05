package ar.edu.utn.dds.k3003.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PdI {

    @Id
    private String id;

    private String hechoId;
    private String descripcion;
    private String lugar;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime momento;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @ElementCollection
    private List<String> etiquetas;

    public PdI(
            String id,
            String hechoId,
            String descripcion,
            String lugar,
            LocalDateTime momento,
            String contenido) {
        this.hechoId = hechoId;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.momento = momento;
        this.contenido = contenido;
    }
}
