package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.model.entity.Product;
import br.com.mvc.model.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String read(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("product", new Product());
        return "products";
    }

    @PostMapping
    public String create(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.create(product);
        redirectAttributes.addFlashAttribute("successMessage",
                "Produto cadastrado com status INDISPONÍVEL e estoque zerado.");
        return "redirect:/products";
    }

    @PostMapping("/{id}/discontinue")
    public String discontinue(
            @PathVariable Long id,
            @RequestParam(defaultValue = "OPERADOR") String role,
            RedirectAttributes redirectAttributes) {
        try {
            productService.discontinue(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "Produto descontinuado com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/products";
    }
}
