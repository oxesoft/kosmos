package kosmos.modelo;

/**
 * Subclasse de Imovel que representa um Terreno da fundação.
 *
 * Demonstra herança e polimorfismo: sobrescreve podeSerAlugado() caso esteja "sem uso" patrimonial.
 */
public class Terreno extends Imovel {

    public Terreno(String endereco, String situacao) {
        super(endereco, situacao);
    }

    @Override
    public String getTipo() {
        return "Terreno";
    }

    /**
     * Polimorfismo: terrenos classificados como "sem uso" patrimonial não podem ser alugados
     * diretamente sem antes passarem para situação disponível ("vago").
     */
    @Override
    public boolean podeSerAlugado() {
        return super.podeSerAlugado() && !"sem uso".equalsIgnoreCase(getSituacao());
    }
}
