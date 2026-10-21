package br.com.hexagonal.domain.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface FaturaService {
    UUID faturar(Long pedidoId, BigDecimal valor);
}