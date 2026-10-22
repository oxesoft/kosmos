package kosmos.modelo;

/**
 * Representa uma Competência mensal (mês/ano) de cobrança dos aluguéis (Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: atributos private (mes, ano, aberta); NÃO existe setAberta(),
 *   pois o estado só pode transitar pelos métodos de ação abrir() e fechar().
 */
public class Competencia {

    private int mes;
    private int ano;
    private boolean aberta; // Sem setter: muda apenas por abrir() e fechar()

    public Competencia(int mes, int ano) {
        setMes(mes);
        setAno(ano);
        this.aberta = true;
    }

    public void abrir() {
        this.aberta = true;
    }

    public void fechar() {
        this.aberta = false;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mês da competência deve estar entre 1 e 12.");
        }
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        if (ano < 2000 || ano > 2100) {
            throw new IllegalArgumentException("Ano de competência inválido.");
        }
        this.ano = ano;
    }

    public boolean isAberta() {
        return aberta;
    }
    // NÃO existe setAberta(): a transição ocorre apenas por abrir() e fechar().

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Competencia)) return false;
        Competencia outra = (Competencia) o;
        return this.mes == outra.mes && this.ano == outra.ano;
    }

    @Override
    public int hashCode() {
        return 31 * ano + mes;
    }

    @Override
    public String toString() {
        return String.format("%02d/%d (%s)", mes, ano, aberta ? "aberta" : "fechada");
    }
}
