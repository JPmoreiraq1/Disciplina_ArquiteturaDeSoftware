package br.com.mvc.model.entity;

public enum StockMovementType {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String label;

    StockMovementType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
