package kosmos.modelo;

/**
 * Interface que representa uma Forma de Pagamento de forma polimórfica (Seção 6 do README.md).
 * Regra 4: "Dinheiro vai para o caixa; PIX, transferência e depósito vão para o banco."
 * O método destino() permite que o registro de recebimento encaminhe o valor sem usar 'if'.
 */
public interface FormaPagamento {

    /**
     * Retorna o destino financeiro do pagamento ("CAIXA" ou "BANCO").
     */
    String destino();

    /**
     * Retorna o nome de apresentação da forma de pagamento.
     */
    String getNome();

    /**
     * Registra polimorficamente a entrada financeira no destino correto (Caixa ou Banco).
     */
    void processarEntrada(Recibo recibo, Caixa caixaDoDia);
}
