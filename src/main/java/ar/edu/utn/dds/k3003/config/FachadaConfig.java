package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.clients.SolicitudesProxy;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPDI;
import ar.edu.utn.dds.k3003.facades.FachadaProcesadorPdI;
import ar.edu.utn.dds.k3003.mocks.FachadaSolicitudesMock;
import ar.edu.utn.dds.k3003.repository.PdIRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FachadaConfig {

    @Bean
    public FachadaProcesadorPDI fachadaProcesadorPdI(PdIRepository repo) {
        Fachada fachada = new Fachada(repo);
        fachada.setFachadaSolicitudes(new SolicitudesProxy(new com.fasterxml.jackson.databind.ObjectMapper()));
        return fachada;
    }
}
