package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.analizadores.*;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInactivoException;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInexistenteException;
import ar.edu.utn.dds.k3003.exceptions.infrastructure.solicitudes.SolicitudesCommunicationException;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.facades.dtos.PdIDTO;
import ar.edu.utn.dds.k3003.metrics.ApplicationMetrics;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.InMemoryPdIRepo;
import ar.edu.utn.dds.k3003.repository.PdIRepository;


import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class Fachada implements FachadaProcesadorPDI {

    private FachadaSolicitudes fachadaSolicitudes;
    @Getter private PdIRepository pdiRepository;
    @Autowired
    private ApplicationMetrics metrics;
    @Autowired
    private Procesador procesador;

    private final AtomicLong generadorID = new AtomicLong(1);

    protected Fachada() {
        this.pdiRepository = new InMemoryPdIRepo();
    }

    @Autowired
    public Fachada(PdIRepository pdiRepository) {
        this.pdiRepository = pdiRepository;
    }

    @Override
    public void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes) {
        this.fachadaSolicitudes = fachadaSolicitudes;
    }

    @Override
    public PdIDTO procesar(PdIDTO pdiDTORecibido) throws HechoInactivoException, HechoInexistenteException  {
        System.out.println("ProcesadorPdI.Fachada.procesar() recibió: " + pdiDTORecibido);
        final String hechoId = pdiDTORecibido.hechoId();
        boolean activo;


        try {
            activo = fachadaSolicitudes.estaActivo(hechoId);
            metrics.incPdisProc();

        } catch (java.util.NoSuchElementException e) {
            // El proxy tira esto si no hay solicitud para ese ID
            throw new HechoInexistenteException(hechoId, e);
        } catch (RuntimeException e) {
            // Timeouts, 5xx, DNS, etc.
            metrics.incError();
            throw new SolicitudesCommunicationException(
                    "Fallo al consultar 'Solicitudes' para hecho " + hechoId + " Error: " + e, e);
        }

        if (!activo) {
            metrics.incError();
            metrics.incErrorAprobacion();
            throw new HechoInactivoException(hechoId);
        }

        PdI nuevoPdI = recibirPdIDTO(pdiDTORecibido);
        System.out.println("ProcesadorPdI.Fachada.procesar() mapeado a entidad: " + nuevoPdI);

        Optional<PdI> yaProcesado =
                pdiRepository.findByHechoId(nuevoPdI.getHechoId()).stream()
                        .filter(
                                p ->
                                        p.getDescripcion().equals(nuevoPdI.getDescripcion())
                                                && p.getLugar().equals(nuevoPdI.getLugar())
                                                && p.getMomento().equals(nuevoPdI.getMomento())
                                                && p.getContenido().equals(nuevoPdI.getContenido()))
                        .findFirst();

        if (yaProcesado.isPresent()) {
            return convertirADTO(yaProcesado.get());
        }

        if(nuevoPdI.getContenido() != null ) {
            procesador.procesar(nuevoPdI);
        }

        pdiRepository.save(nuevoPdI);
        System.out.println("Guardado PdI id=" + nuevoPdI.getId() + " hechoId=" + nuevoPdI.getHechoId());


        System.out.println(
                "Se guardó el PdI con ID "
                        + nuevoPdI.getId()
                        + " en hechoId: "
                        + nuevoPdI.getHechoId());

        PdIDTO pdiDTOAEnviar = convertirADTO(nuevoPdI);

        System.out.println("ProcesadorPdI.Fachada.procesar() responde: " + pdiDTOAEnviar);

        return pdiDTOAEnviar;
    }
    @Override
    public PdIDTO buscarPdIPorId(String idString) {
        metrics.incConsulta();
        Long id = Long.parseLong(idString);
        PdI pdi =
                pdiRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No se encontró el PdI con id: " + id));
        return convertirADTO(pdi);
    }

    @Override
    public List<PdIDTO> buscarPorHecho(String hechoId) {
        metrics.incConsulta();
        List<PdI> lista = pdiRepository.findByHechoId(hechoId);

        System.out.println("Buscando por hechoId: " + hechoId + " - Encontrados: " + lista.size());

        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PdIDTO convertirADTO(PdI pdi) {
        return new PdIDTO(
                String.valueOf(pdi.getId()),
                pdi.getHechoId(),
                pdi.getDescripcion(),
                pdi.getLugar(),
                pdi.getMomento(),
                pdi.getContenido(),
                pdi.getEtiquetas());
    }


    public PdI recibirPdIDTO(PdIDTO pdiDTO) {

        return new PdI(
                pdiDTO.hechoId(),
                pdiDTO.descripcion(),
                pdiDTO.lugar(),
                pdiDTO.momento(),
                pdiDTO.contenido());
    }

    @Override
    public List<PdIDTO> pdis() {
        return this.pdiRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public void borrarTodo() {
        pdiRepository.deleteAll();
        generadorID.set(1); // opcional: reiniciar IDs en memoria
    }
}
