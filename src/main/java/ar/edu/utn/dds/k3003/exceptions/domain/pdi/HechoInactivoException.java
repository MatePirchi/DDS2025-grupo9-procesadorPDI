package ar.edu.utn.dds.k3003.exceptions.domain.pdi;

import ar.edu.utn.dds.k3003.exceptions.base.AppException;
import ar.edu.utn.dds.k3003.exceptions.base.ErrorCode;

public class HechoInactivoException extends AppException {
    public HechoInactivoException(String hechoId) {
        super(ErrorCode.HECHO_INACTIVO, "El hecho " + hechoId + " está inactivo");
    }
}
