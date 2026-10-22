package kosmos.modelo;

/**
 * Classe abstrata base para pessoas físicas no sistema Kosmos (Seção 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: nome e cpf são private e validados nos setters.
 * - Herança: superclasse de Inquilino e Usuario (que por sua vez é superclasse de Gestor e Funcionario).
 */
public abstract class Pessoa {

    // Atributos private para impedir modificação direta sem passar pelas validações de negócio
    private String nome;
    private String cpf;

    public Pessoa(String nome, String cpf) {
        setNome(nome);
        setCpf(cpf);
    }

    public String getNome() {
        return nome;
    }

    /**
     * Valida que o nome não seja nulo nem vazio (Tabela de Encapsulamento - Seção 6).
     */
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da pessoa não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    /**
     * Valida que o CPF contenha exatamente 11 dígitos numéricos (Tabela de Encapsulamento - Seção 6).
     */
    public void setCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("O CPF não pode ser nulo.");
        }
        String apenasDigitos = cpf.replaceAll("\\D", "");
        if (apenasDigitos.length() != 11) {
            throw new IllegalArgumentException("O CPF deve conter exatamente 11 dígitos numéricos.");
        }
        this.cpf = cpf.trim();
    }

    @Override
    public String toString() {
        return nome + " (CPF: " + cpf + ")";
    }
}
