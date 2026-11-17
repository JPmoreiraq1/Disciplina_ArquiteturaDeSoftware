package br.com.mvc.model.entity;

public enum ProductStatus {
    DISPONIVEL("Disponível"),
    INDISPONIVEL("Indisponível"),
    DESCONTINUADO("Descontinuado");

    private final String label;

    ProductStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
