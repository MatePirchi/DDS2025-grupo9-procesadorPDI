package ar.edu.utn.dds.k3003.exceptions.domain.pdi;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class HechoInexistenteException extends AppException {
    public HechoInexistenteException(String hechoId, Throwable cause) {
        super(ErrorCode.HECHO_INEXISTENTE, "No existe el hecho " + hechoId, cause);
    }
}
