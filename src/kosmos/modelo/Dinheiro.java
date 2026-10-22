package kosmos.modelo;

/**
 * Implementação polimórfica de FormaPagamento para pagamento em espécie (Dinheiro).
 * Regra 4: "Dinheiro vai para o caixa".
 */
public class Dinheiro implements FormaPagamento {

    @Override
    public String destino() {
        return "CAIXA";
    }

    @Override
    public String getNome() {
        return "dinheiro";
    }

    /**
     * Polimorfismo: pagamentos em dinheiro geram automaticamente um LancamentoDeRecibo no Caixa.
     */
    @Override
    public void processarEntrada(Recibo recibo, Caixa caixaDoDia) {
        if (caixaDoDia != null) {
            caixaDoDia.lancar(new LancamentoDeRecibo(recibo));
        }
    }

    @Override
    public String toString() {
        return getNome();
    }
}
