package ar.edu.utn.dds.k3003.exceptions.infrastructure.solicitudes;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class SolicitudesCommunicationException extends AppException {
    public SolicitudesCommunicationException(String message, Throwable cause) {
        super(ErrorCode.SOLICITUDES_COMUNICACION, message, cause);
    }
}
