package ar.edu.utn.dds.k3003.config;

import ar.edu.utn.dds.k3003.exceptions.comunicacionexterna.ComunicacionExternaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// Excepciones de tu dominio/infra:
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInactivoException;
import ar.edu.utn.dds.k3003.exceptions.domain.pdi.HechoInexistenteException;
import ar.edu.utn.dds.k3003.exceptions.infrastructure.solicitudes.SolicitudesCommunicationException;

// Micrometer (para exportar a Datadog)


@ControllerAdvice
public class GlobalExceptionHandler {



    public GlobalExceptionHandler() {

    }

    // Maneja errores de JSON mal formado o tipos incompatibles
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errors.put("error", "Malformed JSON");
        
        String message = "El JSON enviado no es válido o tiene tipos de datos incorrectos";
        
        // Intenta extraer un mensaje más específico
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Cannot construct instance")) {
                message = "El formato de algún campo es incorrecto. Verifica que las listas sean arrays [] y no strings";
            } else if (ex.getMessage().contains("Cannot deserialize")) {
                message = "No se pudo deserializar el JSON. Verifica los tipos de datos";
            }
        }
        
        errors.put("message", message);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException e) {
        return buildAndCount(HttpStatus.NOT_FOUND, "NoSuchElementException", "Not Found", e.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Map<String, String>> handleInvalidParameterException(InvalidParameterException e) {
        return buildAndCount(HttpStatus.BAD_REQUEST, "InvalidParameterException", "Bad Request", e.getMessage());
    }


    @ExceptionHandler(HechoInactivoException.class)
    public ResponseEntity<Map<String, String>> handleHechoInactivo(HechoInactivoException e) {
        return buildAndCount(HttpStatus.UNPROCESSABLE_ENTITY, "HechoInactivoException",
                "Hecho Inactivo", e.getMessage());
    }

    @ExceptionHandler(HechoInexistenteException.class)
    public ResponseEntity<Map<String, String>> handleHechoInexistente(HechoInexistenteException e) {
        return buildAndCount(HttpStatus.NOT_FOUND, "HechoInexistenteException",
                "Hecho Inexistente", e.getMessage());
    }

    @ExceptionHandler(SolicitudesCommunicationException.class)
    public ResponseEntity<Map<String, String>> handleSolicitudesCommunication(SolicitudesCommunicationException e) {
        return buildAndCount(HttpStatus.BAD_GATEWAY, "SolicitudesCommunicationException",
                "Solicitudes Communication Error", e.getMessage());
    }

    @ExceptionHandler(ComunicacionExternaException.class)
    public ResponseEntity<Map<String, String>> handleComunicacionExternaException(ComunicacionExternaException e) {
        return buildAndCount(HttpStatus.FAILED_DEPENDENCY, "ComunicacionExternaException",
                "Comunicacion Externa Error", e.getMessage());
    }

    // =======================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        return buildAndCount(HttpStatus.INTERNAL_SERVER_ERROR, e.getClass().getSimpleName(),
                "Internal Server Error", "An unexpected error occurred");
    }

    // Helper: arma respuesta y cuenta métricas {type,status}
    private ResponseEntity<Map<String, String>> buildAndCount(HttpStatus status, String type, String error, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(status.value()));
        response.put("error", error);
        response.put("message", message != null ? message : "");
        return new ResponseEntity<>(response, status);
    }
}
