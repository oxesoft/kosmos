package kosmos.modelo;

/**
 * Representa um código de convite gerado pelo gestor para criação de novas contas.
 *
 * Demonstra:
 * - Encapsulamento (Regra 10): "Conta nova só com código de convite válido e não usado."
 * - O atributo usadoPor não possui setter: só pode ser preenchido uma única vez pelo método de ação usar(...).
 * - O setter setFuncao(...) só permite alterar a função antes de o convite ser utilizado.
 */
public class Convite {

    private final String codigo;
    private String funcao; // "GESTOR" ou "FUNCIONARIO"
    private Usuario usadoPor; // Sem setter direto: muda apenas por usar(...)

    public Convite(String codigo, String funcao) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código do convite não pode ser vazio.");
        }
        this.codigo = codigo.trim();
        setFuncao(funcao);
    }

    /**
     * Método de ação que consome o convite ao criar uma nova conta (Regra 10).
     */
    public void usar(Usuario novoUsuario) {
        if (novoUsuario == null) {
            throw new IllegalArgumentException("O usuário que utiliza o convite não pode ser nulo.");
        }
        if (isUsado()) {
            throw new IllegalStateException("Este convite já foi utilizado por " + usadoPor.getNome() + ".");
        }
        this.usadoPor = novoUsuario;
    }

    public boolean isUsado() {
        return usadoPor != null;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getFuncao() {
        return funcao;
    }

    /**
     * Só permite alterar a função do convite antes de ele ser usado (Seção 6 do README.md).
     */
    public void setFuncao(String funcao) {
        if (isUsado()) {
            throw new IllegalStateException("Não é permitido alterar a função de um convite já utilizado.");
        }
        if (funcao == null || funcao.trim().isEmpty()) {
            throw new IllegalArgumentException("A função do convite não pode ser vazia.");
        }
        this.funcao = funcao.trim().toUpperCase();
    }

    public Usuario getUsadoPor() {
        return usadoPor;
    }
    // Não existe setUsadoPor(): violaria a Regra 10 permitindo reutilizar ou adulterar o convite.
}
