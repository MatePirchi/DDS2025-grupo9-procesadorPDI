package ar.edu.utn.dds.k3003.controller;
import ar.edu.utn.dds.k3003.manejoWorkers.ProcesadorMaster;
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

    private final ProcesadorMaster procesadorMaster;

    @Autowired
    public PdIController(FachadaProcesadorPDI fachadaProcesadorPdI, ProcesadorMaster procesadorMaster) {
        this.fachadaProcesadorPdI = fachadaProcesadorPdI;
        this.procesadorMaster = procesadorMaster;
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
    public ResponseEntity<String> procesarNuevoPdi(@RequestBody PDIDTO req) {
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

        fachadaProcesadorPdI.procesar(entrada);
        // Procesada OK (nueva o duplicada)
        return ResponseEntity.ok("Se ha recibido el PDI correctamente, se ha puesto en la cola de procesamiento");
    }

    @PostMapping("/worker")
    public ResponseEntity<String> guardarNuevoWorker(@RequestBody String req) {
        if (req == null) {
            return ResponseEntity.badRequest().body("url no puede ser vacia");
        }
        procesadorMaster.agregarWorker(req);
        return ResponseEntity.ok("Registrado Correctamente");
    }

    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

}