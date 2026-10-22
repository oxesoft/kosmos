package kosmos.modelo;

/**
 * Representa o perfil Funcionário do sistema Kosmos (ex: Ana Beatriz Lins).
 *
 * Demonstra:
 * - Herança: especializa a classe abstrata Usuario.
 * - Polimorfismo (Regra 9): sobrescreve pode(Acao) impedindo estornar,
 *   fechar exercício e gerenciar pessoas.
 */
public class Funcionario extends Usuario {

    public Funcionario(String nome, String cpf, String email, String senha) {
        super(nome, cpf, email, senha);
    }

    /**
     * Polimorfismo (Regra 9): Funcionário registra recebimentos e lançamentos,
     * mas NÃO estorna, NÃO fecha o exercício e NÃO gerencia pessoas.
     */
    @Override
    public boolean pode(Acao acao) {
        if (acao == null) {
            return false;
        }
        return acao != Acao.ESTORNAR
                && acao != Acao.FECHAR_EXERCICIO
                && acao != Acao.GERENCIAR_PESSOAS;
    }
}
