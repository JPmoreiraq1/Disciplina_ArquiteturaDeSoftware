package br.com.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaUseCase;

@SpringBootTest
@Transactional
class FaturaIntegrationTests {

    @Autowired
    private FaturaUseCase faturaUseCase;

    @Test
    void deveFaturarPedidoEmitindoBoletoNotaFiscalEEmail() {
        UUID faturaId = faturaUseCase.faturar(42L, new BigDecimal("199.90"));

        assertNotNull(faturaId);

        Fatura fatura = faturaUseCase.buscarPorId(faturaId).orElseThrow();
        assertEquals(42L, fatura.getPedidoId());
        assertEquals(0, new BigDecimal("199.90").compareTo(fatura.getValor()));

        // RF02: Código de barras gerado e guardado
        assertNotNull(fatura.getCodigoBarras());
        assertFalse(fatura.getCodigoBarras().isBlank());

        // RF03: Chave de acesso de 44 dígitos guardada
        assertNotNull(fatura.getChaveAcesso());
        assertTrue(fatura.getChaveAcesso().matches("\\d{44}"),
                "Chave de acesso deve ter 44 dígitos, mas foi: " + fatura.getChaveAcesso());

        // RF04: Hash do e-mail guardado
        assertNotNull(fatura.getHashEmail());
        assertEquals(64, fatura.getHashEmail().length());
    }
}
