package ar.edu.utn.dds.k3003.controller;


import ar.edu.utn.dds.k3003.analizadores.*;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaException;
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
    public ResponseEntity<String> wakeyWakey() {
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
        if(this.tieneAlgunParametroNulo(req)){
            return ResponseEntity.badRequest().body( new PDIDTO(null, "Todos los parametros del PdI (excepto las etiquetas y el textoImagen) deben tener algun valor"));
        }
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

        try {
            PDIDTO procesado = fachadaProcesadorPdI.procesar(entrada);
            // Procesada OK (nueva o duplicada)
            return ResponseEntity.ok(procesado);

        }
        catch (ComunicacionExternaException e){
            System.out.println("ComunicacionExternaFallidaException " + e);
            return ResponseEntity.internalServerError().body(new PDIDTO(null, "Error, Ocurrió un error al conectarse con algun ente externo, error: "+ e.getMessage() ));
        }
    }

    private boolean tieneAlgunParametroNulo(PDIDTO elem){
        return  elem.id() == null || elem.hechoId() == null ||
                elem.descripcion() == null || elem.lugar() == null || elem.momento() == null ||
                elem.urlImagen()== null;
    }

    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prueba")
    public ResponseEntity<List<String>> pruebaApiLayer(@RequestBody PDIDTO bod){

        EtiquetadorAPILayer etiquetadorAPILayer = new EtiquetadorAPILayer();
        List<String> algo = etiquetadorAPILayer.obtenerEtiquetas(bod.hechoId());
        return ResponseEntity.ok(algo);

    }
}