package br.com.hexagonal.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.persistence.FaturaRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FaturaPersistenceAdapter implements FaturaRepository {

    private final FaturaSpringDataRepository springDataRepository;

    @Override
    public Fatura salvar(Fatura fatura) {
        FaturaJpaEntity saved = springDataRepository.save(FaturaJpaEntity.fromDomain(fatura));
        return saved.toDomain();
    }

    @Override
    public Optional<Fatura> buscarPorId(UUID id) {
        return springDataRepository.findById(id).map(FaturaJpaEntity::toDomain);
    }
}
