package kosmos.modelo;

/**
 * Representa um Inquilino (locatário) de um imóvel da Fundação Manuel Cruz.
 *
 * Demonstra:
 * - Herança: herda nome e cpf da classe abstrata Pessoa.
 * - Encapsulamento: atributo telefone é private com getter e setter validado.
 */
public class Inquilino extends Pessoa {

    private String telefone;

    public Inquilino(String nome, String cpf, String telefone) {
        super(nome, cpf);
        setTelefone(telefone);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do inquilino não pode ser vazio.");
        }
        this.telefone = telefone.trim();
    }
}
