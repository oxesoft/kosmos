package kosmos.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa o registro histórico de um reajuste de aluguel aplicado a um Contrato
 * (Regra 7 e Seção 5 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: todos os atributos são private final (imutáveis após o registro).
 * - Abstração: implementa a interface Auditavel.
 */
public class Reajuste implements Auditavel {

    private final double valorAnterior;
    private final double valorNovo;
    private final LocalDate aPartirDe;
    private final Usuario feitoPor;
    private final LocalDateTime dataHoraAcao;

    public Reajuste(double valorAnterior, double valorNovo, LocalDate aPartirDe, Usuario feitoPor) {
        if (valorAnterior <= 0 || valorNovo <= 0) {
            throw new IllegalArgumentException("Os valores anterior e novo do reajuste devem ser positivos.");
        }
        if (aPartirDe == null) {
            throw new IllegalArgumentException("A data 'a partir de' do reajuste não pode ser nula.");
        }
        if (feitoPor == null) {
            throw new IllegalArgumentException("O usuário responsável pelo reajuste deve ser informado.");
        }
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.aPartirDe = aPartirDe;
        this.feitoPor = feitoPor;
        this.dataHoraAcao = LocalDateTime.now();
    }

    public double getValorAnterior() {
        return valorAnterior;
    }

    public double getValorNovo() {
        return valorNovo;
    }

    public LocalDate getAPartirDe() {
        return aPartirDe;
    }

    public Usuario getFeitoPor() {
        return feitoPor;
    }

    @Override
    public Usuario getResponsavelAcao() {
        return feitoPor;
    }

    @Override
    public LocalDateTime getDataHoraAcao() {
        return dataHoraAcao;
    }

    @Override
    public String toString() {
        return String.format("Reajuste [De R$ %.2f para R$ %.2f a partir de %s por %s]",
                valorAnterior, valorNovo, aPartirDe, feitoPor.getNome());
    }
}
