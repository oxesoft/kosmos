package kosmos.modelo;

/**
 * Enumeração dos status possíveis de um Prestador cadastrado no Supply.
 */
public enum StatusPrestador {
    ATIVO("ativo"),
    INATIVO("inativo"),
    BLOQUEADO("bloqueado");

    private final String descricao;

    StatusPrestador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
