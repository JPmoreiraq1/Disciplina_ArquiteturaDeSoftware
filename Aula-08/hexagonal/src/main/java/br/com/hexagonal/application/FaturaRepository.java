package br.com.hexagonal.application;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.hexagonal.domain.entities.Fatura;
import br.com.hexagonal.domain.ports.in.FaturaService;
import br.com.hexagonal.domain.ports.out.persistence.FaturaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public  class  FaturaServiceImp implements FaturaService {
    private final FaturaRepository faturaRepository;
    private final BoletoMessaging BoletoMessaging;

    
}

public class FaturaServiceImp implements FaturaService {

    private final FaturaRepository faturaRepository;

    @Override
    public UUID faturarPedido(Long pedidoId, BigDecimal valor) {
        Fatura fatura = Fatura.criar(pedidoId, valor);
        // TOdo: Gerar um boleto
        // TOdo:Emitir Nota
        // TOdo: DIsparar email com boleto e nota
        faturaRepository.salvar(fatura);
        return fatura.getId();
    }

}
