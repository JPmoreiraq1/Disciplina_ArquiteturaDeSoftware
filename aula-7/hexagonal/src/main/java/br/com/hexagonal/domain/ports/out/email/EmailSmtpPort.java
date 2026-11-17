package br.com.hexagonal.domain.ports.out.email;

import br.com.hexagonal.domain.entities.Fatura;

public interface EmailSmtpPort {
    String enviarEmail(Fatura fatura);
}
