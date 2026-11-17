package br.com.mvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.entity.ProductStatus;
import br.com.mvc.model.service.ProductService;
import br.com.mvc.model.service.StockService;

@SpringBootTest
@Transactional
class StockModuleTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @Test
    void newProductStartsUnavailableWithZeroStock() {
        Product created = productService.create(Product.builder()
                .name("Papel A4")
                .price(new BigDecimal("25.00"))
                .build());

        assertEquals(ProductStatus.INDISPONIVEL, created.getStatus());
        assertEquals(0, created.getStockQuantity());
    }

    @Test
    void stockEntryAndExitUpdateProductStatus() {
        Product product = productService.create(Product.builder()
                .name("Caneta Azul")
                .price(new BigDecimal("3.50"))
                .build());

        stockService.registerEntry(product.getId(), 10);
        Product afterEntry = productService.findById(product.getId());
        assertEquals(10, afterEntry.getStockQuantity());
        assertEquals(ProductStatus.DISPONIVEL, afterEntry.getStatus());

        stockService.registerExit(product.getId(), 10);
        Product afterExitToZero = productService.findById(product.getId());
        assertEquals(0, afterExitToZero.getStockQuantity());
        assertEquals(ProductStatus.INDISPONIVEL, afterExitToZero.getStatus());
    }

    @Test
    void cannotExitStockWhenUnavailableOrInsufficient() {
        Product product = productService.create(Product.builder()
                .name("Grampeador")
                .price(new BigDecimal("30.00"))
                .build());

        assertThrows(IllegalStateException.class, () -> stockService.registerExit(product.getId(), 1));

        stockService.registerEntry(product.getId(), 5);
        assertThrows(IllegalStateException.class, () -> stockService.registerExit(product.getId(), 6));
    }

    @Test
    void onlyAdmCanDiscontinueAndDiscontinuedCannotManipulateStock() {
        Product product = productService.create(Product.builder()
                .name("Pasta Suspensa")
                .price(new BigDecimal("12.00"))
                .build());

        assertThrows(IllegalStateException.class, () -> productService.discontinue(product.getId(), "OPERADOR"));

        Product discontinued = productService.discontinue(product.getId(), "ADM");
        assertEquals(ProductStatus.DESCONTINUADO, discontinued.getStatus());

        assertThrows(IllegalStateException.class, () -> stockService.registerEntry(product.getId(), 5));
        assertThrows(IllegalStateException.class, () -> stockService.registerExit(product.getId(), 1));
    }
}
