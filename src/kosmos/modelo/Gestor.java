package kosmos.modelo;

/**
 * Representa o perfil Gestor do sistema Kosmos (ex: Davi Nogueira).
 *
 * Demonstra:
 * - Herança: especializa a classe abstrata Usuario.
 * - Polimorfismo: sobrescreve pode(Acao) concedendo acesso total a todas as operações.
 */
public class Gestor extends Usuario {

    public Gestor(String nome, String cpf, String email, String senha) {
        super(nome, cpf, email, senha);
    }

    /**
     * Polimorfismo: o Gestor possui acesso total ao sistema.
     */
    @Override
    public boolean pode(Acao acao) {
        return acao != null;
    }
}
