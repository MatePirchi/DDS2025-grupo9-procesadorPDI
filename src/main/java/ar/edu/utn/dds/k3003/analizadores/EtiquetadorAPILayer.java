package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.EtiquetadorAPILayerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EtiquetadorAPILayer implements Etiquetador {
    private final EtiquetadorAPILayerProxy proxy;

    public EtiquetadorAPILayer(@Value("${apilayer.apikey}") String apiKey) {
        this.proxy = new EtiquetadorAPILayerProxy(new ObjectMapper(), apiKey);
    }

    @Override
    public List<String> obtenerEtiquetas(String urlImagen) {
        List<String> etiquetas = new ArrayList<>();
        List<EtiquetadorAPILayerDTO> rta = proxy.obtenerEtiquetas(urlImagen);
        rta.forEach(e -> {
            etiquetas.add(e.label());
            System.out.println("Etiqueta " + e.label());
        });

        return etiquetas;
    }

    @Override
    public List<String> procesar(String urlImagen) {
        return obtenerEtiquetas(urlImagen);
    }
}
