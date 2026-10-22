package kosmos.modelo;

/**
 * Enumeração dos status possíveis de uma Solicitação de reparo/serviço.
 */
public enum StatusSolicitacao {
    NA_FILA("na fila"),
    EM_ANDAMENTO("em andamento"),
    BLOQUEADO("bloqueado"),
    FINALIZADO("finalizado");

    private final String descricao;

    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
