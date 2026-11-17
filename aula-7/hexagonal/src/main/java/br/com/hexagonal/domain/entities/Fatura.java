package br.com.hexagonal.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Fatura {
    private final UUID id;
    private final Long pedidoId;
    private final BigDecimal valor;
    private final LocalDateTime emitidoEm;
    private String codigoBarras;
    private String chaveAcesso;
    private String hashEmail;

    @Builder
    private Fatura(
            UUID id,
            Long pedidoId,
            BigDecimal valor,
            LocalDateTime emitidoEm,
            String codigoBarras,
            String chaveAcesso,
            String hashEmail) {

        if (pedidoId == null || pedidoId <= 0) {
            throw new IllegalArgumentException("A fatura precisa de um pedido válido!");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da fatura deve ser maior que zero!");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.pedidoId = pedidoId;
        this.valor = valor;
        this.emitidoEm = emitidoEm != null ? emitidoEm : LocalDateTime.now();
        this.codigoBarras = codigoBarras;
        this.chaveAcesso = chaveAcesso;
        this.hashEmail = hashEmail;
    }

    public static Fatura criar(Long pedidoId, BigDecimal valor) {
        return Fatura.builder()
                .id(UUID.randomUUID())
                .pedidoId(pedidoId)
                .valor(valor)
                .emitidoEm(LocalDateTime.now())
                .build();
    }

    public void inserirBoleto(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("Código de barras do boleto inválido.");
        }
        this.codigoBarras = codigoBarras;
    }

    public void inserirNotaFiscal(String chaveAcesso) {
        if (chaveAcesso == null || !chaveAcesso.matches("\\d{44}")) {
            throw new IllegalArgumentException("A chave de acesso da NF-e deve conter exatamente 44 dígitos.");
        }
        this.chaveAcesso = chaveAcesso;
    }

    public void registrarNotificacaoEmail(String hashEmail) {
        if (hashEmail == null || hashEmail.isBlank()) {
            throw new IllegalArgumentException("Hash do e-mail enviado inválido.");
        }
        this.hashEmail = hashEmail;
    }
}
