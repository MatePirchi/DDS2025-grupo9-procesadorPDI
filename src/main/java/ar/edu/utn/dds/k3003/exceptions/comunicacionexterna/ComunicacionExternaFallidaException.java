package ar.edu.utn.dds.k3003.exceptions.comunicacionexterna;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class ComunicacionExternaFallidaException extends AppException {
    public ComunicacionExternaFallidaException(String message) {
        super(ErrorCode.EXTERNO_COMUNICACION, "Fallo en la Comunicacion con analizador Externo: " + message);
    }
}
