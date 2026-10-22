package kosmos.modelo;

/**
 * Classe abstrata que representa um Imóvel administrado pela Fundação Manuel Cruz
 * (Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: atributos endereco e situacao são private e validados nos setters.
 * - Herança: superclasse de Apartamento, Casa, Loja, Sala e Terreno.
 * - Polimorfismo: métodos getTipo() e podeSerAlugado() sobrescritos pelas subclasses.
 */
public abstract class Imovel {

    private String endereco;
    private String situacao; // Ex: "vago", "alugado", "em reforma", "sem uso"

    public Imovel(String endereco, String situacao) {
        setEndereco(endereco);
        setSituacao(situacao);
    }

    /**
     * Retorna o tipo do imóvel ("Apartamento", "Casa", "Loja", "Sala", "Terreno").
     */
    public abstract String getTipo();

    /**
     * Verifica polimorficamente se o imóvel está apto para locação
     * (por padrão, não pode estar "em reforma" nem "alugado").
     */
    public boolean podeSerAlugado() {
        return !"em reforma".equalsIgnoreCase(situacao)
                && !"alugado".equalsIgnoreCase(situacao);
    }

    public String getEndereco() {
        return endereco;
    }

    /**
     * Valida que o endereço não seja vazio (Tabela de Encapsulamento - Seção 6).
     */
    public void setEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("O endereço do imóvel não pode ser vazio.");
        }
        this.endereco = endereco.trim();
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        if (situacao == null || situacao.trim().isEmpty()) {
            throw new IllegalArgumentException("A situação do imóvel não pode ser vazia.");
        }
        this.situacao = situacao.trim();
    }

    @Override
    public String toString() {
        return String.format("%s [%s - %s]", endereco, getTipo(), situacao);
    }
}
