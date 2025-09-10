package ar.edu.utn.dds.k3003.config;

import java.time.Instant;

public record ApiError(String code, String message, String path, Instant timestamp) {}
