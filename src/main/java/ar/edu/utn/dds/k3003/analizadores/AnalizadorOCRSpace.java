package ar.edu.utn.dds.k3003.analizadores;

import ar.edu.utn.dds.k3003.clients.OCRSpaceProxy;
import ar.edu.utn.dds.k3003.clients.dtos.OCRspaceDTO;
import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.OCRspaceException;
import ar.edu.utn.dds.k3003.model.PdI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AnalizadorOCRSpace implements AnalizadorOCR {

    OCRSpaceProxy proxy;

    public AnalizadorOCRSpace(@Value("${apilayer.apikey}") String apiKey) {
        this.proxy = new OCRSpaceProxy(new ObjectMapper(), apiKey);
    }
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
    public Boolean procesar(String urlImagen, PdI pdi) {
        String texto;
        try {
            texto = analizarImagenURL(urlImagen);
        }catch (OCRspaceException e) {
            System.out.println("Error con OCRspace con url: " + urlImagen);
            return false;
        }
        pdi.setTextoImagen(texto == null ? "" : texto);
        return true;
    }
}
