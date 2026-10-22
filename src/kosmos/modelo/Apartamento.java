package kosmos.modelo;

/**
 * Subclasse de Imovel que representa um Apartamento residencial.
 *
 * Demonstra herança e polimorfismo ao especializar Imovel.
 */
public class Apartamento extends Imovel {

    private String edificio;
    private String numeroApartamento;

    public Apartamento(String endereco, String situacao, String edificio, String numeroApartamento) {
        super(endereco, situacao);
        setEdificio(edificio);
        setNumeroApartamento(numeroApartamento);
    }

    public Apartamento(String endereco, String situacao) {
        super(endereco, situacao);
        this.edificio = "";
        this.numeroApartamento = "";
    }

    @Override
    public String getTipo() {
        return "Apartamento";
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        if (edificio == null) {
            throw new IllegalArgumentException("O nome do edifício não pode ser nulo.");
        }
        this.edificio = edificio.trim();
    }

    public String getNumeroApartamento() {
        return numeroApartamento;
    }

    public void setNumeroApartamento(String numeroApartamento) {
        if (numeroApartamento == null) {
            throw new IllegalArgumentException("O número do apartamento não pode ser nulo.");
        }
        this.numeroApartamento = numeroApartamento.trim();
    }
}
