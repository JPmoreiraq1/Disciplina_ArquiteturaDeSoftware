package br.com.mvc.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.entity.ProductStatus;
import br.com.mvc.model.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product create(Product product) {
        product.setStatus(ProductStatus.INDISPONIVEL);
        product.setStockQuantity(0);
        return productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
    }

    @Transactional
    public Product discontinue(Long productId, String role) {
        if (!"ADM".equalsIgnoreCase(role)) {
            throw new IllegalStateException("Apenas o ADM pode descontinuar um produto.");
        }
        Product product = findById(productId);
        if (product.getStatus() == ProductStatus.DESCONTINUADO) {
            throw new IllegalStateException("O produto já está descontinuado.");
        }
        product.setStatus(ProductStatus.DESCONTINUADO);
        return productRepository.save(product);
    }
}
