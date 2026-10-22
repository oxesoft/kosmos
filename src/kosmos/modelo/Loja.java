package kosmos.modelo;

/**
 * Subclasse de Imovel que representa uma Loja comercial.
 *
 * Demonstra herança e polimorfismo ao especializar Imovel.
 */
public class Loja extends Imovel {

    public Loja(String endereco, String situacao) {
        super(endereco, situacao);
    }

    @Override
    public String getTipo() {
        return "Loja";
    }
}
