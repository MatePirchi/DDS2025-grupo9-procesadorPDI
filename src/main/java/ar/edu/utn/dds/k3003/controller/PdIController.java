package ar.edu.utn.dds.k3003.controller;


import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInactivoException;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pdis")
public class PdIController {

    private final FachadaProcesadorPDI fachadaProcesadorPdI;

    @Autowired
    public PdIController(FachadaProcesadorPDI fachadaProcesadorPdI) {
        this.fachadaProcesadorPdI = fachadaProcesadorPdI;
    }

    @GetMapping
    public ResponseEntity<List<PdIDTO>> listarPdisPorHecho(
            @RequestParam(name = "hecho", required = false) String hechoId) {

        List<PdIDTO> lista = (hechoId != null)
                ? fachadaProcesadorPdI.buscarPorHecho(hechoId)
                : fachadaProcesadorPdI.pdis();

        return ResponseEntity.ok(lista);
    }

    // GET /api/pdis/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PdIDTO> obtenerPdiPorId(@PathVariable String id) {
        PdIDTO dto = fachadaProcesadorPdI.buscarPdIPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PdIDTO> procesarNuevoPdi(@RequestBody PdIDTO req) {
        System.out.println("ProcesadorPdI ← Fuentes (req DTO): " + req);

        PdIDTO entrada = new PdIDTO(
                null,
                req.hechoId(),
                req.descripcion(),
                req.lugar(),
                req.momento(),
                req.contenido(),
                req.etiquetas() == null ? List.of() : req.etiquetas()
        );
        System.out.println("ProcesadorPdI mapea a PdIDTO: " + entrada);

        try {
            PdIDTO procesado = fachadaProcesadorPdI.procesar(entrada);

            String pdiId = procesado.id() != null ? String.valueOf(procesado.id()) : null;
            var etiquetas = (procesado.etiquetas() != null) ? procesado.etiquetas() : List.of();

            // Procesada OK (nueva o duplicada)
            return ResponseEntity.ok(entrada);

        } catch (HechoInactivoException e) {
            return ResponseEntity.ok(req);
        }
    }


    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/prueba")
    public ResponseEntity<PdIDTO> prueba(@RequestBody PdIDTO req) {
        return  ResponseEntity.ok(this.fachadaProcesadorPdI.procesar(new PdIDTO(req.id(), req.hechoId())));

    }
}
