package br.com.mvc.model.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.entity.Sale;
import br.com.mvc.model.entity.SaleStatus;
import br.com.mvc.model.repository.SaleRepository;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final StockService stockService;
    private final BillingHubClient billingHubClient;

    public SaleService(
            SaleRepository saleRepository,
            ProductService productService,
            StockService stockService,
            BillingHubClient billingHubClient) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.stockService = stockService;
        this.billingHubClient = billingHubClient;
    }

    public List<Sale> findAll() {
        return saleRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Sale create(String customerName, Long productId, Integer quantity) {
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        Product product = productService.findById(productId);
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        Sale sale = Sale.builder()
                .customerName(customerName.trim())
                .product(product)
                .quantity(quantity)
                .totalAmount(total)
                .status(SaleStatus.ABERTO)
                .build();
        return saleRepository.save(sale);
    }

    @Transactional
    public Sale closeSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + saleId));

        if (sale.getStatus() == SaleStatus.FECHADO) {
            throw new IllegalStateException("Este pedido já foi fechado.");
        }

        // Baixa no estoque antes de faturar
        stockService.registerExit(sale.getProduct().getId(), sale.getQuantity());

        // Chamada REST ao Hub de Faturamento (Arquitetura Hexagonal)
        UUID faturaId = billingHubClient.faturarPedido(sale.getId(), sale.getTotalAmount());

        sale.setInvoiceId(faturaId);
        sale.setStatus(SaleStatus.FECHADO);
        sale.setClosedAt(LocalDateTime.now());
        return saleRepository.save(sale);
    }
}
