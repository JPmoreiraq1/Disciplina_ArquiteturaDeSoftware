package br.com.hexagonal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaUseCase;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FaturaIntegrationTests {

    @Autowired
    private FaturaUseCase faturaUseCase;

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void deveReceberPedidoViaRestEDevolverIdDaFatura() throws Exception {
        String responseJson = mockMvc.perform(post("/api/faturas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\": 99, \"valor\": 350.50}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.faturaId").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String faturaId = responseJson.replaceAll(".*\"faturaId\"\\s*:\\s*\"([^\"]+)\".*", "$1");

        mockMvc.perform(get("/api/faturas/{id}", faturaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faturaId))
                .andExpect(jsonPath("$.pedidoId").value(99))
                .andExpect(jsonPath("$.codigoBarras").isNotEmpty())
                .andExpect(jsonPath("$.chaveAcesso").isNotEmpty())
                .andExpect(jsonPath("$.hashEmail").isNotEmpty());
    }
}
