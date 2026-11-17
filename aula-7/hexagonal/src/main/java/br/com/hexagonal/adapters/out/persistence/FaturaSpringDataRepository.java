package br.com.hexagonal.adapters.out.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FaturaSpringDataRepository extends JpaRepository<FaturaJpaEntity, UUID> {
}
