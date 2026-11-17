package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.model.service.ProductService;
import br.com.mvc.model.service.StockService;

@Controller
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;
    private final ProductService productService;

    public StockController(StockService stockService, ProductService productService) {
        this.stockService = stockService;
        this.productService = productService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("movements", stockService.findAllMovements());
        return "stock";
    }

    @PostMapping("/entry")
    public String registerEntry(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            stockService.registerEntry(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Entrada de estoque registrada com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/stock";
    }

    @PostMapping("/exit")
    public String registerExit(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            stockService.registerExit(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Saída de estoque registrada com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/stock";
    }
}
