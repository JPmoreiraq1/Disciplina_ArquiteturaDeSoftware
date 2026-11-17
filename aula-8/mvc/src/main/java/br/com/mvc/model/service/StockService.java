package br.com.mvc.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.entity.ProductStatus;
import br.com.mvc.model.entity.StockMovement;
import br.com.mvc.model.entity.StockMovementType;
import br.com.mvc.model.repository.ProductRepository;
import br.com.mvc.model.repository.StockMovementRepository;

@Service
public class StockService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public StockService(ProductRepository productRepository, StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public List<StockMovement> findAllMovements() {
        return stockMovementRepository.findAllByOrderByOccurredAtDescIdDesc();
    }

    @Transactional
    public StockMovement registerEntry(Long productId, Integer quantity) {
        validateQuantity(quantity);
        Product product = findActiveProduct(productId);

        int previousBalance = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int newBalance = previousBalance + quantity;
        product.setStockQuantity(newBalance);

        // Quando o produto sai de zero, ele fica disponível
        if (newBalance > 0) {
            product.setStatus(ProductStatus.DISPONIVEL);
        }

        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(StockMovementType.ENTRADA)
                .quantity(quantity)
                .balanceAfter(newBalance)
                .build();
        return stockMovementRepository.save(movement);
    }

    @Transactional
    public StockMovement registerExit(Long productId, Integer quantity) {
        validateQuantity(quantity);
        Product product = findActiveProduct(productId);

        int currentBalance = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        if (product.getStatus() == ProductStatus.INDISPONIVEL || currentBalance <= 0) {
            throw new IllegalStateException("Produto indisponível: não há estoque disponível para saída.");
        }
        if (currentBalance < quantity) {
            throw new IllegalStateException(
                    "Saldo insuficiente em estoque. Disponível: " + currentBalance + ", solicitado: " + quantity);
        }

        int newBalance = currentBalance - quantity;
        product.setStockQuantity(newBalance);

        // Quando o estoque zera, o produto fica indisponível
        if (newBalance == 0) {
            product.setStatus(ProductStatus.INDISPONIVEL);
        }

        productRepository.save(product);

        StockMovement movement = StockMovement.builder()
                .product(product)
                .type(StockMovementType.SAIDA)
                .quantity(quantity)
                .balanceAfter(newBalance)
                .build();
        return stockMovementRepository.save(movement);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }

    private Product findActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + productId));
        if (product.getStatus() == ProductStatus.DESCONTINUADO) {
            throw new IllegalStateException("Produto descontinuado não pode ser manipulado no estoque.");
        }
        return product;
    }
}
