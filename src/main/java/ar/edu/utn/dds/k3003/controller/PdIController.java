package ar.edu.utn.dds.k3003.controller;


import ar.edu.utn.dds.k3003.analizadores.AnalizadorOCR;
import ar.edu.utn.dds.k3003.analizadores.AnalizadorOCRSpace;
import ar.edu.utn.dds.k3003.analizadores.Etiquetador;
import ar.edu.utn.dds.k3003.analizadores.EtiquetadorAPILayer;
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

            if(procesado.hechoId() == null){
                System.out.println("Hecho de ID: " + entrada.hechoId() + " no esta activo");
                return ResponseEntity.ok(procesado);
            }
            // Procesada OK (nueva o duplicada)
            return ResponseEntity.ok(procesado);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prueba")
    public ResponseEntity<PdIDTO> prueba(@RequestBody PdIDTO req) {
        System.out.println("Entro a Prueba: " + req);
        AnalizadorOCR proxy = new AnalizadorOCRSpace();
        Etiquetador etiq = new EtiquetadorAPILayer();
        System.out.println("Proxys creados");
        String textoEnImagen = proxy.analizarImagenURL("http://dl.a9t9.com/ocrbenchmark/eng.png");
        System.out.println("Imagen Analizada: " + textoEnImagen);
        List<String> etiquetas = etiq.obtenerEtiquetas("http://dl.a9t9.com/ocrbenchmark/eng.png");
        System.out.println("ETIQUETAS: \n" + etiquetas);
        return  ResponseEntity.ok(this.fachadaProcesadorPdI.procesar(new PdIDTO(req.id(), req.hechoId())));

    }
    @PostMapping("/prueba/ocr")
    public ResponseEntity<PdIDTO> pruebaOcr(@RequestBody PdIDTO req) {
        System.out.println("Entro a Prueba");
        AnalizadorOCR proxy = new AnalizadorOCRSpace();
        System.out.println("Proxys creados");
        String textoEnImagen = proxy.analizarImagenURL("http://dl.a9t9.com/ocrbenchmark/eng.png");
        System.out.println("Imagen Analizada: " + textoEnImagen);
        return  ResponseEntity.ok(new PdIDTO(req.id(), req.hechoId()));

    }
}
