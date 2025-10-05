package ar.edu.utn.dds.k3003.controller;


import ar.edu.utn.dds.k3003.analizadores.*;
import ar.edu.utn.dds.k3003.clients.SolicitudesProxy;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaFallidaException;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInexistenteException;
import ar.edu.utn.dds.k3003.exceptions.infrastructure.solicitudes.SolicitudesCommunicationException;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;

import ar.edu.utn.dds.k3003.model.PdI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/pdis")
public class PdIController {

    private final FachadaProcesadorPDI fachadaProcesadorPdI;
    private final Procesador procesador;

    @Autowired
    public PdIController(FachadaProcesadorPDI fachadaProcesadorPdI, Procesador procesador) {
        this.fachadaProcesadorPdI = fachadaProcesadorPdI;
        this.procesador = procesador;
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
        if(this.tieneAlgunParametroNulo(req)){
            return ResponseEntity.badRequest().body( new PdIDTO(null, "Todos los parametros del PdI (excepto las etiquetas) deben tener algun valor"));
        }
        PdIDTO entrada = new PdIDTO(
                req.id(),
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
            // Procesada OK (nueva o duplicada)
            return ResponseEntity.ok(procesado);

        } catch (SolicitudesCommunicationException e) {
            // Timeouts, 5xx, DNS, etc.
            System.out.println("SolicitudesCommunicationException " + e);
            return ResponseEntity.internalServerError().body(new PdIDTO(null, "Error, Ocurrió un error al conectarse con Solicitudes"));
        }
        catch (HechoInexistenteException e) {
            System.out.println("Hecho de ID: " + entrada.hechoId() + " no esta activo");
            return ResponseEntity.unprocessableEntity().body(new PdIDTO(null, "Error, hecho de id" + entrada.hechoId() +" no esta activo" ));
        }
        catch (ComunicacionExternaFallidaException e){
            System.out.println("ComunicacionExternaFallidaException " + e);
            return ResponseEntity.internalServerError().body(new PdIDTO(null, "Error, Ocurrió un error al conectarse con algun ente externo, error: "+ e.getMessage() ));
        }
    }

    private boolean tieneAlgunParametroNulo(PdIDTO elem){
        return  elem.id() == null || elem.hechoId() == null ||
                elem.descripcion() == null || elem.lugar() == null || elem.momento() == null ||
                elem.contenido() == null;
    }

    // DELETE /api/pdis/delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> borrarTodo() {
        fachadaProcesadorPdI.borrarTodo();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/prueba")
    public ResponseEntity<PdIDTO> prueba(@RequestBody PdIDTO req) {
        String url = "http://dl.a9t9.com/ocrbenchmark/eng.png";
        PdI pdi = new PdI(null, null, "2", "algo", null, url);
        procesador.procesar(pdi);
        System.out.println("Texto Imagen: "+ pdi.getContenido()+"\n" +
                "ETIQUETAS: \n" + pdi.getEtiquetas());
        return  ResponseEntity.ok(this.fachadaProcesadorPdI.procesar(new PdIDTO(req.id(), req.hechoId())));

    }
    @PostMapping("/prueba/ocr")
    public ResponseEntity<PdIDTO> pruebaOcr(@RequestBody PdIDTO req) {
        System.out.println("Entro a Prueba");
        String url = "http://dl.a9t9.com/ocrbenchmark/eng.png";
        PdI pdi = new PdI(null, null, "2", "algo", null, url);
        procesador.procesar(pdi);
        System.out.println("Proxys creados");
        return  ResponseEntity.ok(new PdIDTO(req.id(), req.hechoId()));

    }

    @PostMapping("/prueba/sol")
    public ResponseEntity<PdIDTO> pruebaSol(@RequestBody PdIDTO req) {
        SolicitudesProxy proxy = new SolicitudesProxy(new ObjectMapper());
        System.out.println("Entro a Prueba");
        try {
            boolean activo = proxy.estaActivo("1");
            if (activo) {
                System.out.println("Solicitudes activo");
            }
        }
        catch (RuntimeException e) {
            System.out.println("SolicitudesCommunicationException " + e);
        }

        return ResponseEntity.ok(new PdIDTO(req.id(), req.hechoId()));
    }
}
