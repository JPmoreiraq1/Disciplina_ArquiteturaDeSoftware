package br.com.hexagonal.domain.ports.out.persistence;

import java.util.Optional;
import java.util.UUID;

import br.com.hexagonal.domain.entities.Fatura;

public interface FaturaRepository {
    Fatura salvar(Fatura fatura);
    Optional<Fatura> buscarPorId(UUID id);
}
