package br.com.hexagonal.adapters.in.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/faturas")
@RequiredArgsConstructor
public class FaturaController {

    private final FaturaUseCase faturaUseCase;

    @PostMapping
    public ResponseEntity<FaturarPedidoResponse> faturar(@RequestBody FaturarPedidoRequest request) {
        UUID faturaId = faturaUseCase.faturar(request.pedidoId(), request.valor());
        return ResponseEntity.status(HttpStatus.CREATED).body(new FaturarPedidoResponse(faturaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaturaDetalheResponse> buscarPorId(@PathVariable UUID id) {
        return faturaUseCase.buscarPorId(id)
                .map(FaturaDetalheResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record FaturarPedidoRequest(Long pedidoId, BigDecimal valor) {
    }

    public record FaturarPedidoResponse(UUID faturaId) {
    }

    public record FaturaDetalheResponse(
            UUID id,
            Long pedidoId,
            BigDecimal valor,
            LocalDateTime emitidoEm,
            String codigoBarras,
            String chaveAcesso,
            String hashEmail) {

        static FaturaDetalheResponse fromDomain(Fatura fatura) {
            return new FaturaDetalheResponse(
                    fatura.getId(),
                    fatura.getPedidoId(),
                    fatura.getValor(),
                    fatura.getEmitidoEm(),
                    fatura.getCodigoBarras(),
                    fatura.getChaveAcesso(),
                    fatura.getHashEmail());
        }
    }
}
