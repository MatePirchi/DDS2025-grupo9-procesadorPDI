package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.manejoWorkers.*;
import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInactivoException;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInexistenteException;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;
import ar.edu.utn.dds.k3003.facades.FachadaSolicitudes;
import ar.edu.utn.dds.k3003.config.MetricsConfig;
import ar.edu.utn.dds.k3003.model.PdI;
import ar.edu.utn.dds.k3003.repository.InMemoryPdIRepo;
import ar.edu.utn.dds.k3003.repository.PdIRepository;


import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

@Service
public class Fachada implements FachadaProcesadorPDI {

    private FachadaSolicitudes fachadaSolicitudes;
    @Getter private final PdIRepository pdiRepository;
    @Autowired
    private MetricsConfig metrics;
    @Autowired
    private ProcesadorMaster procesador;

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
    public PDIDTO procesar(PDIDTO pdiDTORecibido) throws HechoInactivoException, HechoInexistenteException  {
        System.out.println("ProcesadorPdI.Fachada.procesar() recibió: " + pdiDTORecibido);
        final String hechoId = pdiDTORecibido.hechoId();
        boolean activo;

        try {
            activo = fachadaSolicitudes.estaActivo(hechoId);

        } catch (java.util.NoSuchElementException e) {
            // El proxy tira esto si no hay solicitud para ese ID
            throw new HechoInexistenteException(hechoId, e);
        }

        if (!activo) {
            metrics.incError();
            metrics.incErrorAprobacion();
            throw new HechoInactivoException(hechoId);
        }

        PdI nuevoPdI = recibirPdIDTO(pdiDTORecibido);
        System.out.println("ProcesadorPdI.Fachada.procesar() mapeado a entidad: " + nuevoPdI);

        //Es asincronico, retorna inmediatamente
        procesador.mandar_a_procesar_pdi(nuevoPdI);
        return null;
    }
    @Override
    public PDIDTO buscarPdIPorId(String idString) {
        metrics.incConsulta();
        PdI pdi =
                pdiRepository
                        .findById(idString)
                        .orElseThrow(
                                () ->
                                        new NoSuchElementException(
                                                "No se encontró el PdI con id: " + idString));
        return convertirADTO(pdi);
    }

    @Override
    public List<PDIDTO> buscarPorHecho(String hechoId) {
        metrics.incConsulta();
        List<PdI> lista = pdiRepository.findByHechoId(hechoId);

        System.out.println("Buscando por hechoId: " + hechoId + " - Encontrados: " + lista.size());

        return lista.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public PDIDTO convertirADTO(PdI pdi) {
        return new PDIDTO(
                String.valueOf(pdi.getId()),
                pdi.getHechoId(),
                pdi.getDescripcion(),
                pdi.getLugar(),
                pdi.getMomento(),
                pdi.getUrlImagen(),
                pdi.getTextoImagen(),
                pdi.getEtiquetas());
    }


    public PdI recibirPdIDTO(PDIDTO pdiDTO) {

        return new PdI(
                pdiDTO.id(),
                pdiDTO.hechoId(),
                pdiDTO.descripcion(),
                pdiDTO.lugar(),
                pdiDTO.momento(),
                pdiDTO.urlImagen(),
                pdiDTO.textoImagen(),
                pdiDTO.etiquetas());
    }

    @Override
    public List<PDIDTO> pdis() {
        return this.pdiRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public void borrarTodo() {
        pdiRepository.deleteAll();
    }
}
