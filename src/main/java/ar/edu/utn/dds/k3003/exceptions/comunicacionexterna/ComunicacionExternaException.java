package ar.edu.utn.dds.k3003.exceptions.comunicacionexterna;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class ComunicacionExternaException extends AppException {
    public ComunicacionExternaException(String message) {
        super(ErrorCode.EXTERNO_COMUNICACION, "Fallo en la Comunicacion con analizador Externo: " + message);
    }
}
