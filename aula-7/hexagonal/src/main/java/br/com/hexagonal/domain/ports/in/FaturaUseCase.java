package br.com.hexagonal.domain.ports.in;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import br.com.hexagonal.domain.entities.Fatura;

public interface FaturaUseCase {
    UUID faturar(Long pedidoId, BigDecimal valor);
    Optional<Fatura> buscarPorId(UUID id);
}
