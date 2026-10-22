package kosmos.modelo;

/**
 * Subclasse de Imovel que representa uma Sala comercial (nomenclatura exata da Seção 5 do README.md).
 *
 * Demonstra herança e polimorfismo ao especializar Imovel.
 */
public class Sala extends Imovel {

    public Sala(String endereco, String situacao) {
        super(endereco, situacao);
    }

    @Override
    public String getTipo() {
        return "Sala";
    }
}
