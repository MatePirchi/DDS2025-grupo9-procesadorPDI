package ar.edu.utn.dds.k3003.facades;

import ar.edu.utn.dds.k3003.clients.dtos.PDIDTO;
import java.util.List;
import java.util.NoSuchElementException;

public interface FachadaProcesadorPDI {

    PDIDTO procesar(PDIDTO pdi) throws IllegalStateException;

    PDIDTO buscarPdIPorId(String pdiId) throws NoSuchElementException;

    List<PDIDTO> buscarPorHecho(String hechoId)
            throws NoSuchElementException;

    void setFachadaSolicitudes(FachadaSolicitudes fachadaSolicitudes);

    List<PDIDTO> pdis();

    void borrarTodo();



}