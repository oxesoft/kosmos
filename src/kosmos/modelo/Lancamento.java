package kosmos.modelo;

import java.time.LocalDateTime;

/**
 * Classe abstrata que representa um Lançamento no Caixa (Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento (Regra 8): "Todo lançamento guarda quem fez e quando. Nada se apaga."
 *   Todos os atributos são private final e NÃO existem setters. Correções são feitas com Estorno.
 * - Herança: superclasse de LancamentoDeRecibo, LancamentoManual e Estorno.
 * - Polimorfismo: método abstrato getOrigem(), sobrescrito por cada subclasse.
 * - Abstração: implementa Auditavel e Imprimivel.
 */
public abstract class Lancamento implements Auditavel, Imprimivel {

    private final double valor;
    private final TipoMovimento tipo;
    private final LocalDateTime hora;
    private final Usuario lancadoPor;

    public Lancamento(double valor, TipoMovimento tipo, Usuario lancadoPor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do lançamento deve ser maior que zero.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo de movimento (ENTRADA ou SAIDA) não pode ser nulo.");
        }
        if (lancadoPor == null) {
            throw new IllegalArgumentException("O usuário responsável pelo lançamento não pode ser nulo.");
        }
        this.valor = valor;
        this.tipo = tipo;
        this.hora = LocalDateTime.now();
        this.lancadoPor = lancadoPor;
    }

    /**
     * Método polimórfico que retorna a coluna "Origem" da tela de Caixa
     * ("recibo ...", "manual" ou "estorno").
     */
    public abstract String getOrigem();

    /**
     * Retorna a descrição para a coluna "Histórico" da tela de Caixa.
     */
    public abstract String getHistorico();

    @Override
    public Usuario getResponsavelAcao() {
        return lancadoPor;
    }

    @Override
    public LocalDateTime getDataHoraAcao() {
        return hora;
    }

    @Override
    public String gerarDocumento() {
        return String.format("[%s] %s | Origem: %s | %s: R$ %.2f | Por: %s",
                hora.toLocalTime().withNano(0),
                getHistorico(),
                getOrigem(),
                tipo.getDescricao(),
                valor,
                lancadoPor.getNome());
    }

    // ========================================================================
    // Apenas Getters (Sem Setters por Regra 8: corrige-se com Estorno)
    // ========================================================================

    public double getValor() {
        return valor;
    }

    public TipoMovimento getTipo() {
        return tipo;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public Usuario getLancadoPor() {
        return lancadoPor;
    }

    @Override
    public String toString() {
        return gerarDocumento();
    }
}
