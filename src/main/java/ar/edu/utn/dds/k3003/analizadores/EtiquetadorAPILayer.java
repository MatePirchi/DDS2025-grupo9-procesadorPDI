package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.EtiquetadorAPILayerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ApiLayerException;
import ar.edu.utn.dds.k3003.model.PdI;
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
    public Boolean procesar(String urlImagen, PdI pdi) {
        List<String> etiquetas;
        try {
            etiquetas = obtenerEtiquetas(urlImagen);
        }catch (ApiLayerException e) {
            System.out.println("Error con ApiLayer con url: " + urlImagen);
            return false;
        }
        pdi.setEtiquetas(etiquetas == null ? List.of() : etiquetas);
        return true;
    }
}
