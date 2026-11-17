package br.com.hexagonal.adapters.out.email;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.out.email.EmailSmtpPort;

@Component
public class EmailSmtpAdapter implements EmailSmtpPort {

    private static final Logger log = LoggerFactory.getLogger(EmailSmtpAdapter.class);

    @Override
    public String enviarEmail(Fatura fatura) {
        String conteudoEmail = String.format(
                "Fatura %s | Pedido %d | Valor R$ %s | Boleto %s | NF-e %s",
                fatura.getId(),
                fatura.getPedidoId(),
                fatura.getValor(),
                fatura.getCodigoBarras(),
                fatura.getChaveAcesso());

        String hash = gerarSha256(conteudoEmail);
        log.info("[SMTP EMAIL] Notificação enviada para fatura={} pedido={} hash={}",
                fatura.getId(), fatura.getPedidoId(), hash);
        return hash;
    }

    private String gerarSha256(String conteudo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(conteudo.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
