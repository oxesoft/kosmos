package kosmos.modelo;

/**
 * Implementação polimórfica de FormaPagamento para Depósito bancário.
 * Regra 4: "PIX, transferência e depósito vão para o banco."
 */
public class Deposito implements FormaPagamento {

    @Override
    public String destino() {
        return "BANCO";
    }

    @Override
    public String getNome() {
        return "depósito";
    }

    @Override
    public void processarEntrada(Recibo recibo, Caixa caixaDoDia) {
        // Vai diretamente para a conta bancária; não altera o caixa físico do dia.
    }

    @Override
    public String toString() {
        return getNome();
    }
}
