package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.EtiquetadorAPILayerProxy;
import ar.edu.utn.dds.k3003.clients.dtos.EtiquetadorAPILayerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class EtiquetadorAPILayer implements Etiquetador{
    EtiquetadorAPILayerProxy proxy = new EtiquetadorAPILayerProxy(new ObjectMapper());
    @Override
    public List<String> obtenerEtiquetas(String urlImagen) {
        List<String> etiquetas = new ArrayList<>();
        List<EtiquetadorAPILayerDTO> rta = proxy.obtenerEtiquetas(urlImagen);
        rta.stream().filter( e-> e.confidence()> 0.60)
                    .forEach(e -> etiquetas.add(e.label()));

        return etiquetas;
    }
}
