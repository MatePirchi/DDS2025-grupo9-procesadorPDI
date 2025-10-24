package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.OCRSpaceProxy;
import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class AnalizadorOCRSpace implements AnalizadorOCR {
    OCRSpaceProxy proxy = new OCRSpaceProxy(new ObjectMapper());
    
    @Override
    public String analizarImagenURL(String imagenURL) {
        OCRspaceDTO rta = proxy.hacerPedidoAOCRSpace(imagenURL);
        if (rta.ParsedResults().size() > 1)
            System.out.println("WARNING: OCRSpaceProxy recibio el analisis de mas de 1 imagen, pero se envio solo 1 imagen ");

        if (rta.ParsedResults().get(0).ParsedText().isEmpty())
            return "";

        assert rta.ParsedResults().get(0).TextOverlay() != null;
        if (!rta.ParsedResults().get(0).TextOverlay().Lines().isEmpty())
            System.out.println("WARNING: OCRSpaceProxy recibio el Text Overlay de alguna imagen");

        return rta.ParsedResults().get(0).ParsedText();
    }

    @Override
    public List<String> procesar(String urlImagen) {
        String texto = analizarImagenURL(urlImagen);
        return texto.isEmpty() ? Collections.emptyList() : Collections.singletonList(texto);
    }

}
