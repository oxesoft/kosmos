package kosmos.modelo;

/**
 * Subclasse de Lancamento criada manualmente pelo operador no Caixa
 * (ex: compra de material de limpeza ou depósito bancário).
 *
 * Demonstra:
 * - Herança: especializa a classe abstrata Lancamento.
 * - Polimorfismo: sobrescreve getOrigem() devolvendo "manual".
 */
public class LancamentoManual extends Lancamento {

    private final String historico;

    public LancamentoManual(double valor, TipoMovimento tipo, String historico, Usuario lancadoPor) {
        super(valor, tipo, lancadoPor);
        if (historico == null || historico.trim().isEmpty()) {
            throw new IllegalArgumentException("O histórico do lançamento manual não pode ser vazio.");
        }
        this.historico = historico.trim();
    }

    @Override
    public String getOrigem() {
        return "manual";
    }

    @Override
    public String getHistorico() {
        return historico;
    }
}
