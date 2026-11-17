package br.com.hexagonal.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import br.com.hexagonal.domain.entities.Fatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fatura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaturaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "emitido_em", nullable = false)
    private LocalDateTime emitidoEm;

    @Column(name = "codigo_barras", nullable = false, length = 64)
    private String codigoBarras;

    @Column(name = "chave_acesso", nullable = false, length = 44)
    private String chaveAcesso;

    @Column(name = "hash_email", nullable = false, length = 64)
    private String hashEmail;

    public static FaturaJpaEntity fromDomain(Fatura fatura) {
        return FaturaJpaEntity.builder()
                .id(fatura.getId())
                .pedidoId(fatura.getPedidoId())
                .valor(fatura.getValor())
                .emitidoEm(fatura.getEmitidoEm())
                .codigoBarras(fatura.getCodigoBarras())
                .chaveAcesso(fatura.getChaveAcesso())
                .hashEmail(fatura.getHashEmail())
                .build();
    }

    public Fatura toDomain() {
        return Fatura.builder()
                .id(this.id)
                .pedidoId(this.pedidoId)
                .valor(this.valor)
                .emitidoEm(this.emitidoEm)
                .codigoBarras(this.codigoBarras)
                .chaveAcesso(this.chaveAcesso)
                .hashEmail(this.hashEmail)
                .build();
    }
}
