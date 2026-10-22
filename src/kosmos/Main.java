package kosmos;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import kosmos.modelo.Acao;
import kosmos.modelo.Apartamento;
import kosmos.modelo.Caixa;
import kosmos.modelo.Casa;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.Convite;
import kosmos.modelo.DiaFechadoException;
import kosmos.modelo.Dinheiro;
import kosmos.modelo.Imovel;
import kosmos.modelo.Inquilino;
import kosmos.modelo.Lancamento;
import kosmos.modelo.LancamentoManual;
import kosmos.modelo.Loja;
import kosmos.modelo.Pix;
import kosmos.modelo.Prestador;
import kosmos.modelo.Recibo;
import kosmos.modelo.Sala;
import kosmos.modelo.Solicitacao;
import kosmos.modelo.StatusPrestador;
import kosmos.modelo.StatusSolicitacao;
import kosmos.modelo.Terreno;
import kosmos.modelo.TipoMovimento;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;
import kosmos.servico.AutenticacaoService;
import kosmos.servico.BuscaService;
import kosmos.servico.CaixaService;
import kosmos.servico.CompetenciaService;
import kosmos.servico.RecebimentoService;

/**
 * Classe principal de demonstração do sistema Kosmos seguindo fielmente o README.md.
 */
public class Main {

    public static void main(String[] args) {
        KosmosRepositorio repo = new KosmosRepositorio();
        repo.setCaixaAtual(new Caixa(3180.00));

        AutenticacaoService authService = new AutenticacaoService(repo);
        RecebimentoService recebimentoService = new RecebimentoService(repo);
        CompetenciaService competenciaService = new CompetenciaService(repo);
        CaixaService caixaService = new CaixaService(repo);
        BuscaService buscaService = new BuscaService(repo);

        // 1. Convites e Criação de Usuários (Regra 9 e Regra 10)
        Convite conviteGestor = new Convite("KSM-GESTOR-01", "GESTOR");
        Convite conviteFunc = new Convite("KSM-FUNC-01", "FUNCIONARIO");
        repo.adicionarConvite(conviteGestor);
        repo.adicionarConvite(conviteFunc);

        Usuario davi = authService.cadastrar(
                "KSM-GESTOR-01", "Davi Nogueira", "111.222.333-44", "davi@kosmos.org.br", "senha123");
        Usuario ana = authService.cadastrar(
                "KSM-FUNC-01", "Ana Beatriz Lins", "234.567.890-11", "ana@kosmos.org.br", "senha456");

        System.out.println("=== 1. POLIMORFISMO EM USUÁRIO (pode(Acao)) ===");
        System.out.println(davi.getNome() + " pode ESTORNAR? " + davi.pode(Acao.ESTORNAR));
        System.out.println(ana.getNome() + " pode ESTORNAR? " + ana.pode(Acao.ESTORNAR));

        // 2. Hierarquia de Imóveis (Apartamento, Casa, Loja, Sala, Terreno)
        Imovel apto101 = new Apartamento("Ed. São Jorge · apto 101", "vago", "Ed. São Jorge", "101");
        Imovel casa14 = new Casa("Rua Coronel Bento, 14", "vago");
        Imovel loja1 = new Loja("Ed. Santo Antônio · loja 1", "vago");
        Imovel sala5 = new Sala("Ed. Santo Antônio · sala 5", "vago");
        Imovel terreno3 = new Terreno("Loteamento Norte, quadra 3", "sem uso");

        repo.adicionarImovel(apto101);
        repo.adicionarImovel(casa14);
        repo.adicionarImovel(loja1);
        repo.adicionarImovel(sala5);
        repo.adicionarImovel(terreno3);

        // 3. Inquilinos, Contratos e Reajustes (Regra 7: aluguel só muda por reajuste)
        Inquilino marta = new Inquilino("Marta Ribeiro", "555.666.777-88", "(85) 99999-0001");
        repo.adicionarInquilino(marta);

        Contrato contratoMarta = new Contrato(
                apto101, marta, 1240.00, 5, LocalDate.now().plusYears(1));
        repo.adicionarContrato(contratoMarta);

        Competencia out2026 = competenciaService.abrir(
                new Competencia(10, 2026),
                Map.of(contratoMarta, 1300.00),
                davi
        );
        System.out.println("\n=== 2. REAJUSTE REGISTRADO NO CONTRATO (Regra 7) ===");
        System.out.println("Novo aluguel: R$ " + contratoMarta.getValorAluguel());
        System.out.println("Histórico   : " + contratoMarta.getReajustes());

        // 4. Recebimento e Polimorfismo de FormaPagamento (Regras 2, 3 e 4)
        Competencia ago2026 = new Competencia(8, 2026);
        Competencia set2026 = new Competencia(9, 2026);

        // Pagando 2 competências em Dinheiro -> gera 2 recibos e lança automaticamente no Caixa
        List<Recibo> recibosDinheiro = recebimentoService.registrar(
                contratoMarta,
                Arrays.asList(ago2026, set2026),
                new Dinheiro(),
                ana,
                LocalDate.of(2026, 9, 10)
        );

        // Pagando 1 competência em PIX -> vai para o Banco
        List<Recibo> recibosPix = recebimentoService.registrar(
                contratoMarta,
                Collections.singletonList(out2026),
                new Pix(),
                ana,
                LocalDate.of(2026, 10, 5)
        );

        System.out.println("\n=== 3. RECIBOS EMITIDOS (Regras 1, 2, 3 e 4) ===");
        for (Recibo r : recibosDinheiro) {
            System.out.print(r.gerarDocumento());
        }
        for (Recibo r : recibosPix) {
            System.out.print(r.gerarDocumento());
        }

        // 5. Caixa, Lançamentos e Estorno (Regras 5, 6, 8 e 9)
        LancamentoManual despesaLimpeza = new LancamentoManual(
                340.00, TipoMovimento.SAIDA, "Material de limpeza · residencial", davi);
        repo.getCaixaAtual().lancar(despesaLimpeza);

        // Estorno feito pelo Gestor
        caixaService.estornar(despesaLimpeza, "Valor lançado em duplicidade", davi);

        System.out.println("\n=== 4. MOVIMENTO DE CAIXA (Polimorfismo em Lancamento.getOrigem()) ===");
        for (Lancamento l : repo.getCaixaAtual().getLancamentos()) {
            System.out.println(l);
        }
        System.out.printf("Saldo em Caixa: R$ %.2f | Saldo no Banco: R$ %.2f%n",
                repo.getCaixaAtual().saldo(), repo.getSaldoBanco());

        // Conferir e fechar o dia (Regra 5 e 6)
        caixaService.conferirEFechar(repo.getCaixaAtual().saldo(), "", davi);
        try {
            repo.getCaixaAtual().lancar(new LancamentoManual(50.0, TipoMovimento.SAIDA, "Café", davi));
        } catch (DiaFechadoException e) {
            System.out.println("-> [Regra 5 validada]: " + e.getMessage());
        }

        // 6. Supply e Solicitações
        Prestador reformaFacil = new Prestador(
                "Reforma Fácil Serviços Ltda", "12.345.678/0001-90",
                "Empresa", "Terceirizado", "Reparos e pequenas obras", StatusPrestador.ATIVO);
        repo.adicionarPrestador(reformaFacil);

        Solicitacao solicitacao = new Solicitacao(apto101, "Troca da fechadura", reformaFacil);
        solicitacao.mudarStatus(StatusSolicitacao.EM_ANDAMENTO, davi);
        repo.adicionarSolicitacao(solicitacao);

        System.out.println("\n=== 5. SOLICITAÇÃO E BUSCA UNIFICADA ===");
        System.out.println(solicitacao);
        System.out.println("Resultados da busca por 'Marta': " + buscaService.buscar("Marta").size() + " item(ns).");
    }
}
