package kosmos.modelo;

/**
 * Enumeração dos tipos de movimento financeiro no Caixa (Entrada ou Saída).
 */
public enum TipoMovimento {
    ENTRADA("Entrada"),
    SAIDA("Saída");

    private final String descricao;

    TipoMovimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
