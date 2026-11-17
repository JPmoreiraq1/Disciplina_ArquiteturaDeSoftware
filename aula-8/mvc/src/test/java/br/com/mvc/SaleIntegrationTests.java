package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.entity.ProductStatus;
import br.com.mvc.model.entity.Sale;
import br.com.mvc.model.entity.SaleStatus;
import br.com.mvc.model.service.BillingHubClient;
import br.com.mvc.model.service.ProductService;
import br.com.mvc.model.service.SaleService;
import br.com.mvc.model.service.StockService;

@SpringBootTest
@Transactional
class SaleIntegrationTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @Autowired
    private SaleService saleService;

    @MockitoBean
    private BillingHubClient billingHubClient;

    @Test
    void fecharPedidoEnviaAoHubEGuardaIdDaFatura() {
        Product product = productService.create(Product.builder()
                .name("Resma Papel A4")
                .price(new BigDecimal("30.00"))
                .build());
        stockService.registerEntry(product.getId(), 5);

        Sale sale = saleService.create("Dunder Mifflin", product.getId(), 2);
        assertEquals(SaleStatus.ABERTO, sale.getStatus());

        UUID faturaEsperada = UUID.randomUUID();
        when(billingHubClient.faturarPedido(eq(sale.getId()), any(BigDecimal.class)))
                .thenReturn(faturaEsperada);

        Sale closed = saleService.closeSale(sale.getId());

        assertEquals(SaleStatus.FECHADO, closed.getStatus());
        assertEquals(faturaEsperada, closed.getInvoiceId());
        assertNotNull(closed.getClosedAt());

        Product updatedProduct = productService.findById(product.getId());
        assertEquals(3, updatedProduct.getStockQuantity());
        assertEquals(ProductStatus.DISPONIVEL, updatedProduct.getStatus());
    }
}
