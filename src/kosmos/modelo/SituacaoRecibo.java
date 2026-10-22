package kosmos.modelo;

/**
 * Enumeração das situações possíveis de um Recibo no sistema Kosmos.
 */
public enum SituacaoRecibo {
    NAO_EMITIDO("não emitido"),
    EMITIDO_NAO_PAGO("emitido, não pago"),
    PAGO("pago"),
    EM_ATRASO("em atraso");

    private final String descricao;

    SituacaoRecibo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
