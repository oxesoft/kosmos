package kosmos.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o Caixa diário da Fundação Manuel Cruz (Seções 3, 5 e 6 do README.md).
 *
 * Demonstra encapsulamento rigoroso:
 * - saldo() é sempre calculado a partir dos lançamentos, nunca guardado num atributo alterável.
 * - getLancamentos() devolve lista somente leitura (Collections.unmodifiableList).
 * - fechado NÃO possui setter: só muda pelo método de ação conferir(contado, justificativa).
 * - Regra 5: "Dia fechado não aceita lançamento novo" (lança DiaFechadoException).
 * - Regra 6: "Divergência na conferência exige justificativa."
 */
public class Caixa {

    private final List<Lancamento> lancamentos;
    private final double saldoAnterior;
    private boolean fechado; // Sem setter: muda apenas por conferir(...)
    private Double valorContadoConferencia;
    private String justificativaConferencia;

    public Caixa(double saldoAnterior) {
        if (saldoAnterior < 0) {
            throw new IllegalArgumentException("O saldo anterior do caixa não pode ser negativo.");
        }
        this.saldoAnterior = saldoAnterior;
        this.lancamentos = new ArrayList<>();
        this.fechado = false;
    }

    /**
     * Registra um lançamento no caixa.
     * Regra 5: recusa lançamento novo se o dia já estiver fechado (lança DiaFechadoException).
     */
    public void lancar(Lancamento lancamento) {
        if (fechado) {
            throw new DiaFechadoException("Dia fechado não aceita lançamento novo no caixa.");
        }
        if (lancamento == null) {
            throw new IllegalArgumentException("O lançamento não pode ser nulo.");
        }
        this.lancamentos.add(lancamento);
    }

    /**
     * Calcula o saldo atual a partir do saldo anterior e dos lançamentos registrados.
     * Nunca é armazenado em um atributo editável (Seção 6 do README.md).
     */
    public double saldo() {
        double atual = saldoAnterior;
        for (Lancamento l : lancamentos) {
            if (l.getTipo() == TipoMovimento.ENTRADA) {
                atual += l.getValor();
            } else {
                atual -= l.getValor();
            }
        }
        return Math.round(atual * 100.0) / 100.0;
    }

    /**
     * Confere e fecha o movimento do dia.
     * Regra 6: "Divergência na conferência exige justificativa."
     */
    public void conferir(double contado, String justificativa) {
        if (fechado) {
            throw new DiaFechadoException("O caixa deste dia já foi conferido e fechado.");
        }
        if (contado < 0) {
            throw new IllegalArgumentException("O valor contado na conferência não pode ser negativo.");
        }
        double saldoCalculado = saldo();
        boolean houveDivergencia = Math.abs(contado - saldoCalculado) >= 0.01;
        if (houveDivergencia && (justificativa == null || justificativa.trim().isEmpty())) {
            throw new IllegalArgumentException(
                    String.format("Divergência na conferência (Esperado: R$ %.2f, Contado: R$ %.2f) exige justificativa.",
                            saldoCalculado, contado));
        }
        this.valorContadoConferencia = contado;
        this.justificativaConferencia = (justificativa != null) ? justificativa.trim() : "";
        this.fechado = true;
    }

    public double getSaldoAnterior() {
        return saldoAnterior;
    }

    /**
     * Devolve lista somente de leitura para impedir adição/remoção fora de lancar(...) (Seção 6).
     */
    public List<Lancamento> getLancamentos() {
        return Collections.unmodifiableList(lancamentos);
    }

    public boolean isFechado() {
        return fechado;
    }
    // NÃO existe setFechado(): violaria as Regras 5 e 6.

    public Double getValorContadoConferencia() {
        return valorContadoConferencia;
    }

    public String getJustificativaConferencia() {
        return justificativaConferencia;
    }
}
