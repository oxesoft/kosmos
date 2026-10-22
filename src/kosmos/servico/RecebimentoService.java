package kosmos.servico;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import kosmos.modelo.Acao;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.FormaPagamento;
import kosmos.modelo.Recibo;
import kosmos.modelo.SituacaoRecibo;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;

/**
 * Serviço responsável pelo fluxo de registro de recebimento de aluguéis (Seção 5 do README.md).
 *
 * Regras garantidas:
 * - Regra 2: "Um recibo por competência. Se o inquilino paga 3 meses atrasados, saem 3 recibos."
 * - Regra 3: "Multa e juros são calculados, não digitados: 2% de multa + 0,033% ao dia de atraso."
 * - Regra 4 (Polimorfismo sem 'if' de forma de pagamento): FormaPagamento.processarEntrada(...)
 *   e FormaPagamento.destino() encaminham Dinheiro para o Caixa e PIX/Transferência/Depósito para o Banco.
 */
public class RecebimentoService {

    private final KosmosRepositorio repositorio;

    public RecebimentoService(KosmosRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repositorio = repositorio;
    }

    public List<Recibo> registrar(Contrato contrato, List<Competencia> competencias,
                                  FormaPagamento forma, Usuario usuario) {
        return registrar(contrato, competencias, forma, usuario, LocalDate.now());
    }

    public List<Recibo> registrar(Contrato contrato, List<Competencia> competencias,
                                  FormaPagamento forma, Usuario usuario, LocalDate dataPagamento) {
        if (contrato == null) {
            throw new IllegalArgumentException("O contrato não pode ser nulo.");
        }
        if (competencias == null || competencias.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos uma competência para registrar o recebimento.");
        }
        if (forma == null) {
            throw new IllegalArgumentException("A forma de pagamento não pode ser nula.");
        }
        if (usuario == null || !usuario.pode(Acao.REGISTRAR_RECEBIMENTO)) {
            throw new IllegalArgumentException("Usuário inválido ou sem permissão para registrar recebimentos.");
        }

        List<Recibo> recibosGerados = new ArrayList<>();

        // Regra 2: gera um recibo individual para cada competência paga
        for (Competencia comp : competencias) {
            LocalDate dataVencimento = LocalDate.of(comp.getAno(), comp.getMes(), contrato.getDiaVencimento());
            int diasAtraso = 0;
            if (dataPagamento.isAfter(dataVencimento)) {
                diasAtraso = (int) ChronoUnit.DAYS.between(dataVencimento, dataPagamento);
            }

            String numero = repositorio.proximoNumeroRecibo(comp.getAno());
            Recibo recibo = new Recibo(
                    numero,
                    contrato,
                    comp,
                    diasAtraso,
                    forma,
                    SituacaoRecibo.PAGO,
                    usuario
            );

            // Polimorfismo (Seção 6): a própria FormaPagamento encaminha para o Caixa se for Dinheiro,
            // sem nenhum 'if (forma instanceof ...)' para decidir se cria LancamentoDeRecibo no Caixa!
            forma.processarEntrada(recibo, repositorio.getCaixaAtual());

            if ("BANCO".equals(forma.destino())) {
                repositorio.creditarNoBanco(recibo.getValorTotal());
            }

            repositorio.adicionarRecibo(recibo);
            recibosGerados.add(recibo);
        }

        return recibosGerados;
    }
}
