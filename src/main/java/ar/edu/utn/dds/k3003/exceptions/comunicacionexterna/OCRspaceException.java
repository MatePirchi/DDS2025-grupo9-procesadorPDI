package ar.edu.utn.dds.k3003.exceptions.comunicacionexterna;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class OCRspaceException extends AppException {
    public OCRspaceException(String message) {
        super(ErrorCode.EXTERNO_COMUNICACION, "Fallo en la Comunicacion con OCRspace: " + message);
    }
}
