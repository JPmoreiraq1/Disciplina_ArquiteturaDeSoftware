package br.com.hexagonal.application;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaUseCase;
import br.com.hexagonal.domain.ports.out.email.EmailSmtpPort;
import br.com.hexagonal.domain.ports.out.messaging.BoletoQueuePort;
import br.com.hexagonal.domain.ports.out.persistence.FaturaRepository;
import br.com.hexagonal.domain.ports.out.soap.NotaFiscalSoapPort;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaturaServiceImpl implements FaturaUseCase {

    private final FaturaRepository faturaRepository;
    private final BoletoQueuePort boletoQueuePort;
    private final NotaFiscalSoapPort notaFiscalSoapPort;
    private final EmailSmtpPort emailSmtpPort;

    @Override
    public UUID faturar(Long pedidoId, BigDecimal valor) {
        Fatura fatura = Fatura.criar(pedidoId, valor);

        // RF02: Emitir boleto por fila e guardar código de barras
        String codigoBarras = boletoQueuePort.emitirBoleto(fatura);
        fatura.inserirBoleto(codigoBarras);

        // RF03: Emitir nota fiscal por SOAP e guardar chave de acesso (44 dígitos)
        String chaveAcesso = notaFiscalSoapPort.emitirNotaFiscal(fatura);
        fatura.inserirNotaFiscal(chaveAcesso);

        // RF04: Notificar cliente por e-mail SMTP e guardar hash do conteúdo
        String hashEmail = emailSmtpPort.enviarEmail(fatura);
        fatura.registrarNotificacaoEmail(hashEmail);

        // RF01: Persistir a fatura e devolver o ID
        Fatura faturaSalva = faturaRepository.salvar(fatura);
        return faturaSalva.getId();
    }

    @Override
    public Optional<Fatura> buscarPorId(UUID id) {
        return faturaRepository.buscarPorId(id);
    }
}
