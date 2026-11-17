package br.com.hexagonal.adapters.out.messaging;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.messaging.BoletoQueuePort;

@Component
public class BoletoQueueAdapter implements BoletoQueuePort {

    private static final Logger log = LoggerFactory.getLogger(BoletoQueueAdapter.class);

    @Override
    public String emitirBoleto(Fatura fatura) {
        String valorCentavos = fatura.getValor()
                .multiply(BigDecimal.valueOf(100))
                .toBigInteger()
                .toString();
        String valorFormatado = String.format("%10s", valorCentavos).replace(' ', '0');
        long fatorAleatorio = ThreadLocalRandom.current().nextLong(100000000000L, 999999999999L);

        // Linha digitável / código de barras simulado (47 dígitos)
        String codigoBarras = String.format("34191%010d%012d%s",
                fatura.getPedidoId(),
                fatorAleatorio,
                valorFormatado);
        if (codigoBarras.length() > 47) {
            codigoBarras = codigoBarras.substring(0, 47);
        } else if (codigoBarras.length() < 47) {
            codigoBarras = String.format("%-47s", codigoBarras).replace(' ', '0');
        }

        log.info("[FILA BOLETO] Mensagem publicada para fatura={} pedido={} codigoBarras={}",
                fatura.getId(), fatura.getPedidoId(), codigoBarras);
        return codigoBarras;
    }
}
