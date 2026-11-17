package br.com.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.mvc.model.service.ProductService;
import br.com.mvc.model.service.SaleService;

@Controller
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;
    private final ProductService productService;

    public SaleController(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("sales", saleService.findAll());
        model.addAttribute("products", productService.findAll());
        return "sales";
    }

    @PostMapping
    public String create(
            @RequestParam String customerName,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {
        try {
            saleService.create(customerName, productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Pedido criado em aberto com sucesso.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/sales";
    }

    @PostMapping("/{id}/close")
    public String closeSale(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var closed = saleService.closeSale(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Pedido fechado e faturado! ID da Fatura: " + closed.getInvoiceId());
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao fechar pedido: " + ex.getMessage());
        }
        return "redirect:/sales";
    }
}
