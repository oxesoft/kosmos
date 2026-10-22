package kosmos.modelo;

/**
 * Subclasse de Imovel que representa uma Casa residencial.
 *
 * Demonstra herança e polimorfismo ao especializar Imovel.
 */
public class Casa extends Imovel {

    public Casa(String endereco, String situacao) {
        super(endereco, situacao);
    }

    @Override
    public String getTipo() {
        return "Casa";
    }
}
