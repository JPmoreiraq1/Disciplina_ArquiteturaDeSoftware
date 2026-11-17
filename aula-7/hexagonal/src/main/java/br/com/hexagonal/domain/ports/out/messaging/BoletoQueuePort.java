package br.com.hexagonal.domain.ports.out.messaging;

import br.com.hexagonal.domain.entities.Fatura;

public interface BoletoQueuePort {
    String emitirBoleto(Fatura fatura);
}
