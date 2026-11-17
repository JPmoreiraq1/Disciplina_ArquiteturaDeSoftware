package br.com.hexagonal.adapters.out.soap;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoapPort;

@Component
public class NotaFiscalSoapAdapter implements NotaFiscalSoapPort {

    private static final Logger log = LoggerFactory.getLogger(NotaFiscalSoapAdapter.class);
    private static final DateTimeFormatter AAMM = DateTimeFormatter.ofPattern("yyMM");

    @Override
    public String emitirNotaFiscal(Fatura fatura) {
        String uf = "35";
        String aamm = fatura.getEmitidoEm().format(AAMM);
        String cnpjSimulado = "12345678000199";
        String modelo = "55";
        String serie = "001";
        String numeroNf = String.format("%09d", fatura.getPedidoId() % 1_000_000_000L);
        String tipoEmissao = "1";
        String codigoNumerico = String.format("%08d", ThreadLocalRandom.current().nextInt(10_000_000, 99_999_999));
        String dv = String.valueOf(ThreadLocalRandom.current().nextInt(0, 10));

        // 2 + 4 + 14 + 2 + 3 + 9 + 1 + 8 + 1 = 44 dígitos
        String chaveAcesso = uf + aamm + cnpjSimulado + modelo + serie + numeroNf + tipoEmissao + codigoNumerico + dv;

        log.info("[SOAP NF-e] Nota fiscal emitida para fatura={} pedido={} chaveAcesso={}",
                fatura.getId(), fatura.getPedidoId(), chaveAcesso);
        return chaveAcesso;
    }
}
