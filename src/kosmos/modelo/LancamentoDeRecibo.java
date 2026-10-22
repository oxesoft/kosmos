package kosmos.modelo;

/**
 * Subclasse de Lancamento gerada automaticamente a partir do pagamento de um Recibo em dinheiro.
 *
 * Demonstra:
 * - Herança: especializa a classe abstrata Lancamento.
 * - Polimorfismo: sobrescreve getOrigem() devolvendo o código do recibo (ex: "recibo 2026/0812").
 */
public class LancamentoDeRecibo extends Lancamento {

    private final Recibo recibo;

    public LancamentoDeRecibo(Recibo recibo) {
        super(validarRecibo(recibo).getValorTotal(), TipoMovimento.ENTRADA, recibo.getEmitidoPor());
        this.recibo = recibo;
    }

    private static Recibo validarRecibo(Recibo recibo) {
        if (recibo == null) {
            throw new IllegalArgumentException("O recibo de origem do lançamento não pode ser nulo.");
        }
        return recibo;
    }

    @Override
    public String getOrigem() {
        return "recibo " + recibo.getNumero();
    }

    @Override
    public String getHistorico() {
        return "Aluguel " + recibo.getCompetencia().getMes() + "/" + recibo.getCompetencia().getAno()
                + " · " + recibo.getContrato().getImovel().getEndereco();
    }

    public Recibo getRecibo() {
        return recibo;
    }
}
