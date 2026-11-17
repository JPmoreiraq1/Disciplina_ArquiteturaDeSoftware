package br.com.mvc.model.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BillingHubClient {

    private final RestClient restClient;

    public BillingHubClient(
            @Value("${billing.hub.url:http://localhost:8081}") String billingHubUrl) {
        this.restClient = RestClient.builder().baseUrl(billingHubUrl).build();
    }

    public UUID faturarPedido(Long pedidoId, BigDecimal valor) {
        FaturarPedidoRequest request = new FaturarPedidoRequest(pedidoId, valor);
        FaturarPedidoResponse response = restClient.post()
                .uri("/api/faturas")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(FaturarPedidoResponse.class);

        if (response == null || response.faturaId() == null) {
            throw new IllegalStateException("Hub de faturamento não retornou o ID da fatura.");
        }
        return response.faturaId();
    }

    public record FaturarPedidoRequest(Long pedidoId, BigDecimal valor) {
    }

    public record FaturarPedidoResponse(UUID faturaId) {
    }
}
