package kosmos.modelo;

import java.time.LocalDateTime;

/**
 * Representa um Recibo de aluguel emitido pelo sistema Kosmos (Seções 3, 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento estrito (Regra 1): "Recibo não se edita depois de emitido. Erro se corrige com estorno."
 *   Todos os atributos são private final e NÃO existem setters. Qualquer tentativa de alteração
 *   lança ReciboImutavelException.
 * - Regra 3: "Multa e juros são calculados, não digitados: 2% de multa + 0,033% ao dia de atraso."
 * - Abstração: implementa as interfaces Auditavel e Imprimivel.
 */
public class Recibo implements Auditavel, Imprimivel {

    public static final double PERCENTUAL_MULTA = 0.02;        // 2% de multa
    public static final double PERCENTUAL_JUROS_DIA = 0.00033; // 0,033% ao dia de atraso

    private final String numero;
    private final Contrato contrato;
    private final Competencia competencia;
    private final double valor;
    private final double encargos;
    private final FormaPagamento forma;
    private final SituacaoRecibo situacao;
    private final Usuario emitidoPor;
    private final LocalDateTime emitidoEm;

    /**
     * Constrói um recibo emitido e pago, calculando automaticamente os encargos (multa + juros)
     * com base nos dias de atraso (Regra 3).
     */
    public Recibo(String numero, Contrato contrato, Competencia competencia,
                  int diasAtraso, FormaPagamento forma, SituacaoRecibo situacao,
                  Usuario emitidoPor) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("O número do recibo não pode ser vazio.");
        }
        if (contrato == null) {
            throw new IllegalArgumentException("O contrato vinculado ao recibo não pode ser nulo.");
        }
        if (competencia == null) {
            throw new IllegalArgumentException("A competência do recibo não pode ser nula.");
        }
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("Os dias de atraso não podem ser negativos.");
        }
        if (forma == null) {
            throw new IllegalArgumentException("A forma de pagamento do recibo não pode ser nula.");
        }
        if (situacao == null) {
            throw new IllegalArgumentException("A situação do recibo não pode ser nula.");
        }
        if (emitidoPor == null) {
            throw new IllegalArgumentException("O usuário emissor do recibo não pode ser nulo.");
        }

        this.numero = numero.trim();
        this.contrato = contrato;
        this.competencia = competencia;
        this.valor = contrato.getValorAluguel();
        this.encargos = calcularEncargos(this.valor, diasAtraso);
        this.forma = forma;
        this.situacao = situacao;
        this.emitidoPor = emitidoPor;
        this.emitidoEm = LocalDateTime.now();
    }

    /**
     * Regra 3: "Multa e juros são calculados, não digitados: 2% de multa + 0,033% ao dia de atraso."
     */
    public static double calcularEncargos(double valorBase, int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        double multa = valorBase * PERCENTUAL_MULTA;
        double juros = valorBase * (PERCENTUAL_JUROS_DIA * diasAtraso);
        return Math.round((multa + juros) * 100.0) / 100.0;
    }

    public double calcularEncargos(int diasAtraso) {
        return calcularEncargos(this.valor, diasAtraso);
    }

    public double getValorTotal() {
        return valor + encargos;
    }

    /**
     * Regra 1: caso algum fluxo tente modificar um recibo já emitido, lança ReciboImutavelException.
     */
    public void alterarReciboEmitido() {
        throw new ReciboImutavelException(
                "Recibo " + numero + " não pode ser editado depois de emitido. Corrija por meio de estorno.");
    }

    @Override
    public String gerarDocumento() {
        return String.format(
                "=== RECIBO Nº %s ===%n"
                + "Imóvel      : %s%n"
                + "Inquilino   : %s%n"
                + "Competência : %s%n"
                + "Aluguel     : R$ %.2f%n"
                + "Encargos    : R$ %.2f%n"
                + "Total Pago  : R$ %.2f (%s -> %s)%n"
                + "Emitido por : %s em %s%n",
                numero,
                contrato.getImovel().getEndereco(),
                contrato.getInquilino().getNome(),
                competencia,
                valor,
                encargos,
                getValorTotal(),
                forma.getNome(),
                forma.destino(),
                emitidoPor.getNome(),
                emitidoEm);
    }

    @Override
    public Usuario getResponsavelAcao() {
        return emitidoPor;
    }

    @Override
    public LocalDateTime getDataHoraAcao() {
        return emitidoEm;
    }

    // ========================================================================
    // Apenas Getters (Recibo emitido é imutável - Sem Setters por Regra 1)
    // ========================================================================

    public String getNumero() {
        return numero;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public double getValor() {
        return valor;
    }

    public double getEncargos() {
        return encargos;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public SituacaoRecibo getSituacao() {
        return situacao;
    }

    public Usuario getEmitidoPor() {
        return emitidoPor;
    }

    public LocalDateTime getEmitidoEm() {
        return emitidoEm;
    }
}
