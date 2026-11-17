package br.com.mvc.model.entity;

public enum SaleStatus {
    ABERTO("Aberto"),
    FECHADO("Fechado");

    private final String label;

    SaleStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
