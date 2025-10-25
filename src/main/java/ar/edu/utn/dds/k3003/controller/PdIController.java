package ar.edu.utn.dds.k3003.controller;


import ar.edu.utn.dds.k3003.analizadores.*;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;

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
    public ResponseEntity<List<PDIDTO>> listarPdisPorHecho(
            @RequestParam(name = "hecho", required = false) String hechoId) {

        List<PDIDTO> lista = (hechoId != null)
                ? fachadaProcesadorPdI.buscarPorHecho(hechoId)
                : fachadaProcesadorPdI.pdis();
        return ResponseEntity.ok(lista);

    }
    @GetMapping("/wake")
    public ResponseEntity<String> wakeUP() {
        //No hace nada, hecho para despertar
        return ResponseEntity.ok("Me Desperte");
    }

    // GET /api/pdis/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PDIDTO> obtenerPdiPorId(@PathVariable String id) {
        PDIDTO dto = fachadaProcesadorPdI.buscarPdIPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<PDIDTO> procesarNuevoPdi(@RequestBody PDIDTO req) {
        System.out.println("ProcesadorPdI ← Fuentes (req DTO): " + req);
        
        PDIDTO entrada = new PDIDTO(
                req.id(),
                req.hechoId(),
                req.descripcion(),
                req.lugar(),
                req.momento(),
                req.urlImagen(),
                req.textoImagen(),
                req.etiquetas() == null ? List.of() : req.etiquetas()
        );
        System.out.println("ProcesadorPdI mapea a PdIDTO: " + entrada);

        PDIDTO procesado = fachadaProcesadorPdI.procesar(entrada);
        // Procesada OK (nueva o duplicada)
        return ResponseEntity.ok(procesado);
    }

    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

}