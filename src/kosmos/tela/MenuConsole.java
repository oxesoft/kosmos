package kosmos.tela;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import kosmos.modelo.Acao;
import kosmos.modelo.Apartamento;
import kosmos.modelo.Auditavel;
import kosmos.modelo.Caixa;
import kosmos.modelo.Casa;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.Convite;
import kosmos.modelo.Deposito;
import kosmos.modelo.DiaFechadoException;
import kosmos.modelo.Dinheiro;
import kosmos.modelo.Estorno;
import kosmos.modelo.FormaPagamento;
import kosmos.modelo.Imovel;
import kosmos.modelo.Inquilino;
import kosmos.modelo.Lancamento;
import kosmos.modelo.LancamentoManual;
import kosmos.modelo.Loja;
import kosmos.modelo.Pix;
import kosmos.modelo.Prestador;
import kosmos.modelo.Recibo;
import kosmos.modelo.ReciboImutavelException;
import kosmos.modelo.Sala;
import kosmos.modelo.Solicitacao;
import kosmos.modelo.StatusPrestador;
import kosmos.modelo.StatusSolicitacao;
import kosmos.modelo.Terreno;
import kosmos.modelo.TipoMovimento;
import kosmos.modelo.Transferencia;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;
import kosmos.servico.AutenticacaoService;
import kosmos.servico.BuscaService;
import kosmos.servico.CaixaService;
import kosmos.servico.CompetenciaService;
import kosmos.servico.RecebimentoService;

/**
 * Interface interativa em console com formulários para todos os fluxos e regras de negócio
 * da plataforma Kosmos (README.md).
 */
public class MenuConsole {

    private final KosmosRepositorio repositorio;
    private final AutenticacaoService authService;
    private final RecebimentoService recebimentoService;
    private final CompetenciaService competenciaService;
    private final CaixaService caixaService;
    private final BuscaService buscaService;

    private Usuario usuarioLogado;
    private final Scanner scanner;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MenuConsole(KosmosRepositorio repositorio, Usuario usuarioInicial) {
        this.repositorio = repositorio;
        this.authService = new AutenticacaoService(repositorio);
        this.recebimentoService = new RecebimentoService(repositorio);
        this.competenciaService = new CompetenciaService(repositorio);
        this.caixaService = new CaixaService(repositorio);
        this.buscaService = new BuscaService(repositorio);
        this.usuarioLogado = usuarioInicial;
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirCabecalhoPrincipal();
            System.out.println(" [1]  💵 REGISTRAR RECEBIMENTO DE ALUGUEL (Formulário do Modal)");
            System.out.println(" [2]  🔍 BUSCA UNIFICADA (Imóveis, Inquilinos e Recibos)");
            System.out.println(" [3]  💰 MOVIMENTO DE CAIXA (Extrato, Lançamento Manual, Estorno)");
            System.out.println(" [4]  🔒 CONFERIR E FECHAR O DIA (Conferência com Justificativa e RDD)");
            System.out.println(" [5]  📅 ABRIR O MÊS SEGUINTE & REAJUSTAR ALUGUEL (Competência e Reajustes)");
            System.out.println(" [6]  🛠️  SOLICITAÇÕES DE REPARO (Nova Solicitação e Mudança de Status)");
            System.out.println(" [7]  👥 SUPPLY (Funcionários e Prestadores Terceirizados)");
            System.out.println(" [8]  🏢 IMÓVEIS, INQUILINOS & CONTRATOS (Cadastros e Locações)");
            System.out.println(" [9]  📜 RECIBOS, PRESTAÇÃO DE CONTAS & AUDITORIA");
            System.out.println(" [10] 👤 USUÁRIOS & ACESSO (Troca de Usuário, Login, Convites)");
            System.out.println(" [0]  🚪 SAIR");
            System.out.println("================================================================================");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine().trim();
            System.out.println();

            switch (opcao) {
                case "1" -> fluxoRegistrarRecebimento();
                case "2" -> fluxoBusca();
                case "3" -> fluxoCaixa();
                case "4" -> fluxoConferirCaixa();
                case "5" -> fluxoAbrirMesReajuste();
                case "6" -> fluxoSolicitacoes();
                case "7" -> fluxoSupply();
                case "8" -> fluxoImoveisInquilinosContratos();
                case "9" -> fluxoRecibosAuditoria();
                case "10" -> fluxoUsuariosAcesso();
                case "0" -> {
                    System.out.println("Encerrando o Kosmos. Até logo!");
                    executando = false;
                }
                default -> {
                    System.out.println("Opção inválida. Pressione ENTER para continuar...");
                    scanner.nextLine();
                }
            }
        }
    }

    private void exibirCabecalhoPrincipal() {
        Caixa caixa = repositorio.getCaixaAtual();
        String statusCaixa = caixa.isFechado() ? "FECHADO" : "ABERTO";
        String perfil = (usuarioLogado instanceof kosmos.modelo.Gestor) ? "Gestor" : "Funcionária";

        System.out.println("\n================================================================================");
        System.out.println("                     KOSMOS · GESTÃO IMOBILIÁRIA E CAIXA                        ");
        System.out.println("                  Fundação Manuel Cruz · Sistema em Java (POO)                  ");
        System.out.println("================================================================================");
        System.out.printf("👤 Sessão atual: %s (%s) | E-mail: %s%n",
                usuarioLogado.getNome(), perfil, usuarioLogado.getEmail());
        System.out.printf("💵 Caixa de Hoje: R$ %,.2f [%s] | 🏦 Saldo Banco: R$ %,.2f%n",
                caixa.saldo(), statusCaixa, repositorio.getSaldoBanco());
        System.out.println("--------------------------------------------------------------------------------");
    }

    // ========================================================================
    // FLUXO 1: REGISTRAR RECEBIMENTO DE ALUGUEL
    // ========================================================================
    private void fluxoRegistrarRecebimento() {
        System.out.println("--- [MODAL: REGISTRAR RECEBIMENTO DE ALUGUEL] ---");
        List<Contrato> contratos = repositorio.getContratos();
        if (contratos.isEmpty()) {
            System.out.println("Nenhum contrato ativo cadastrado.");
            pausar();
            return;
        }

        System.out.println("Contratos de Locação Ativos:");
        for (int i = 0; i < contratos.size(); i++) {
            Contrato c = contratos.get(i);
            System.out.printf(" [%d] %s | Inquilino: %s | Aluguel: R$ %,.2f | Vence dia: %d%n",
                    i + 1, c.getImovel().getEndereco(), c.getInquilino().getNome(),
                    c.getValorAluguel(), c.getDiaVencimento());
        }

        int idxContrato = lerInteiro("Selecione o número do contrato (ou 0 para cancelar): ", 0, contratos.size());
        if (idxContrato == 0) return;
        Contrato contratoEscolhido = contratos.get(idxContrato - 1);

        List<Competencia> comps = repositorio.getCompetencias();
        if (comps.isEmpty()) {
            System.out.println("Nenhuma competência cadastrada no sistema.");
            pausar();
            return;
        }

        System.out.println("\nCompetências Cadastradas:");
        for (int i = 0; i < comps.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, comps.get(i));
        }
        System.out.print("Digite o(s) número(s) da(s) competência(s) a pagar (ex: '1' ou '1, 2' para pagar meses atrasados): ");
        String linhaComps = scanner.nextLine().trim();
        List<Competencia> compsEscolhidas = new ArrayList<>();
        for (String pedaco : linhaComps.split(",")) {
            try {
                int idx = Integer.parseInt(pedaco.trim());
                if (idx >= 1 && idx <= comps.size()) {
                    compsEscolhidas.add(comps.get(idx - 1));
                }
            } catch (NumberFormatException ignored) {}
        }
        if (compsEscolhidas.isEmpty()) {
            System.out.println("Nenhuma competência válida selecionada.");
            pausar();
            return;
        }

        LocalDate dataPagamento = lerData("Data do pagamento (DD/MM/AAAA ou ENTER para hoje): ", LocalDate.now());

        System.out.println("\nSelecione a Forma de Pagamento:");
        System.out.println(" [1] Dinheiro (Entra no Caixa e gera lançamento automático no dia)");
        System.out.println(" [2] PIX (Entra na conta bancária da fundação)");
        System.out.println(" [3] Transferência bancária (Entra no banco)");
        System.out.println(" [4] Depósito bancário (Entra no banco)");
        int opcaoForma = lerInteiro("Escolha (1-4): ", 1, 4);

        FormaPagamento forma = switch (opcaoForma) {
            case 1 -> new Dinheiro();
            case 2 -> new Pix();
            case 3 -> new Transferencia();
            default -> new Deposito();
        };

        // REVISÃO ANTES DE CONFIRMAR (conforme fluxo de modais do Kosmos)
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                             REVISÃO DO RECEBIMENTO                             ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Imóvel       : " + contratoEscolhido.getImovel().getEndereco());
        System.out.println("Inquilino    : " + contratoEscolhido.getInquilino().getNome());
        System.out.println("Aluguel Base : R$ " + String.format("%,.2f", contratoEscolhido.getValorAluguel()));
        System.out.println("Forma Pagto  : " + forma.getNome() + " (Destino: " + forma.destino() + ")");
        System.out.println("Data Pagto   : " + dataPagamento.format(FORMATO_DATA));
        System.out.println("Competências : " + compsEscolhidas.size() + " recibo(s) a emitir:");

        double valorTotalGeral = 0.0;
        for (Competencia comp : compsEscolhidas) {
            LocalDate vencimento = LocalDate.of(comp.getAno(), comp.getMes(), contratoEscolhido.getDiaVencimento());
            int diasAtraso = (int) Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(vencimento, dataPagamento));
            double encargos = Recibo.calcularEncargos(contratoEscolhido.getValorAluguel(), diasAtraso);
            double totalRecibo = contratoEscolhido.getValorAluguel() + encargos;
            valorTotalGeral += totalRecibo;
            System.out.printf("  • %02d/%d | Vencimento: %s | Atraso: %d dias | Encargos: R$ %,.2f | Total: R$ %,.2f%n",
                    comp.getMes(), comp.getAno(), vencimento.format(FORMATO_DATA), diasAtraso, encargos, totalRecibo);
        }
        System.out.printf("VALOR TOTAL CONSOLIDADO: R$ %,.2f%n", valorTotalGeral);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.print("Confirmar e registrar o recebimento? (S/N): ");
        String confirmacao = scanner.nextLine().trim();
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Recebimento cancelado.");
            pausar();
            return;
        }

        try {
            List<Recibo> recibosGerados = recebimentoService.registrar(
                    contratoEscolhido, compsEscolhidas, forma, usuarioLogado, dataPagamento);

            System.out.println("\n✅ RECEBIMENTO(S) REGISTRADO(S) COM SUCESSO!");
            for (Recibo r : recibosGerados) {
                System.out.println("\n" + r.gerarDocumento());
            }
        } catch (Exception e) {
            System.out.println("\n❌ ERRO ao registrar recebimento: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 2: BUSCA UNIFICADA
    // ========================================================================
    private void fluxoBusca() {
        System.out.println("--- [BUSCA UNIFICADA] ---");
        System.out.print("Digite o termo de busca (imóvel, inquilino, CPF, número do recibo): ");
        String termo = scanner.nextLine().trim();

        List<Object> resultados = buscaService.buscar(termo);
        if (resultados.isEmpty()) {
            System.out.println("Nenhum resultado encontrado para '" + termo + "'.");
        } else {
            System.out.printf("%nResultados encontrados (%d):%n", resultados.size());
            for (Object obj : resultados) {
                if (obj instanceof Imovel im) {
                    System.out.println(" [IMÓVEL]    " + im);
                } else if (obj instanceof Inquilino inq) {
                    System.out.println(" [INQUILINO] " + inq.getNome() + " | CPF: " + inq.getCpf() + " | Tel: " + inq.getTelefone());
                } else if (obj instanceof Recibo rc) {
                    System.out.println(" [RECIBO]    Nº " + rc.getNumero() + " | " + rc.getContrato().getImovel().getEndereco()
                            + " | Inquilino: " + rc.getContrato().getInquilino().getNome() + " | R$ " + rc.getValorTotal());
                }
            }
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 3: MOVIMENTO DE CAIXA
    // ========================================================================
    private void fluxoCaixa() {
        boolean voltar = false;
        while (!voltar) {
            Caixa caixa = repositorio.getCaixaAtual();
            System.out.println("\n--- [MOVIMENTO DE CAIXA] ---");
            System.out.printf("Saldo Anterior: R$ %,.2f | Saldo Atual: R$ %,.2f | Estado: %s%n",
                    caixa.getSaldoAnterior(), caixa.saldo(), caixa.isFechado() ? "FECHADO" : "ABERTO");
            System.out.println(" [1] Visualizar Extrato Detalhado");
            System.out.println(" [2] Novo Lançamento Manual (Entrada / Saída)");
            System.out.println(" [3] Estornar Lançamento (Exige perfil Gestor)");
            System.out.println(" [0] Voltar ao menu principal");
            System.out.print("Escolha: ");

            String op = scanner.nextLine().trim();
            switch (op) {
                case "1" -> exibirExtratoCaixa();
                case "2" -> novoLancamentoManual();
                case "3" -> estornarLancamento();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void exibirExtratoCaixa() {
        Caixa caixa = repositorio.getCaixaAtual();
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                           EXTRATO DO CAIXA DO DIA                              ");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Saldo Inicial: R$ %,.2f%n", caixa.getSaldoAnterior());
        List<Lancamento> lancamentos = caixa.getLancamentos();
        if (lancamentos.isEmpty()) {
            System.out.println("Nenhum lançamento registrado no caixa de hoje.");
        } else {
            double totalEntradas = 0.0;
            double totalSaidas = 0.0;
            for (int i = 0; i < lancamentos.size(); i++) {
                Lancamento l = lancamentos.get(i);
                if (l.getTipo() == TipoMovimento.ENTRADA) totalEntradas += l.getValor();
                else totalSaidas += l.getValor();
                System.out.printf(" [%02d] %s%n", i + 1, l.gerarDocumento());
            }
            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("Total de Entradas: R$ %,.2f | Total de Saídas: R$ %,.2f%n", totalEntradas, totalSaidas);
        }
        System.out.printf("SALDO ATUAL DO CAIXA: R$ %,.2f%n", caixa.saldo());
        pausar();
    }

    private void novoLancamentoManual() {
        System.out.println("\n--- [MODAL: NOVO LANÇAMENTO MANUAL NO CAIXA] ---");
        System.out.println("Tipo de Lançamento:");
        System.out.println(" [1] Entrada (+)");
        System.out.println(" [2] Saída (-)");
        int t = lerInteiro("Escolha (1 ou 2): ", 1, 2);
        TipoMovimento tipo = (t == 1) ? TipoMovimento.ENTRADA : TipoMovimento.SAIDA;

        double valor = lerDoublePositivo("Valor do lançamento (R$): ");
        System.out.print("Histórico / Descrição: ");
        String historico = scanner.nextLine().trim();

        try {
            LancamentoManual lancamento = new LancamentoManual(valor, tipo, historico, usuarioLogado);
            repositorio.getCaixaAtual().lancar(lancamento);
            repositorio.registrarAuditoria(lancamento);
            System.out.println("✅ Lançamento efetuado com sucesso! Novo saldo do caixa: R$ "
                    + String.format("%,.2f", repositorio.getCaixaAtual().saldo()));
        } catch (DiaFechadoException e) {
            System.out.println("❌ Bloqueado pela Regra 5: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
        pausar();
    }

    private void estornarLancamento() {
        System.out.println("\n--- [MODAL: ESTORNAR LANÇAMENTO] ---");
        Caixa caixa = repositorio.getCaixaAtual();
        List<Lancamento> lancs = caixa.getLancamentos();
        if (lancs.isEmpty()) {
            System.out.println("Nenhum lançamento disponível para estorno.");
            pausar();
            return;
        }

        System.out.println("Lançamentos do dia:");
        for (int i = 0; i < lancs.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, lancs.get(i).gerarDocumento());
        }

        int idx = lerInteiro("Número do lançamento a estornar (ou 0 para cancelar): ", 0, lancs.size());
        if (idx == 0) return;

        Lancamento alvo = lancs.get(idx - 1);
        System.out.print("Justificativa obrigatória do estorno: ");
        String justificativa = scanner.nextLine().trim();

        try {
            Estorno estorno = caixaService.estornar(alvo, justificativa, usuarioLogado);
            System.out.println("✅ Estorno realizado com sucesso pelo Gestor!");
            System.out.println(estorno.gerarDocumento());
            System.out.printf("Novo saldo do caixa: R$ %,.2f%n", caixa.saldo());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Bloqueado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 4: CONFERIR E FECHAR O DIA (COM RDD)
    // ========================================================================
    private void fluxoConferirCaixa() {
        System.out.println("--- [MODAL: CONFERIR CAIXA E FECHAR O DIA] ---");
        Caixa caixa = repositorio.getCaixaAtual();
        if (caixa.isFechado()) {
            System.out.println("O caixa de hoje já está fechado.");
            System.out.printf("Valor contado na conferência: R$ %,.2f%n", caixa.getValorContadoConferencia());
            if (!caixa.getJustificativaConferencia().isEmpty()) {
                System.out.println("Justificativa de divergência: " + caixa.getJustificativaConferencia());
            }
            pausar();
            return;
        }

        double saldoEsperado = caixa.saldo();
        System.out.printf("Saldo esperado pelo sistema em dinheiro: R$ %,.2f%n", saldoEsperado);
        double contado = lerDoublePositivo("Informe o valor físico total contado na gaveta (R$): ");

        String justificativa = "";
        if (Math.abs(contado - saldoEsperado) >= 0.01) {
            System.out.printf("⚠️ Houve divergência de R$ %,.2f entre o contado e o esperado!%n", contado - saldoEsperado);
            System.out.print("Regra 6: Digite a justificativa obrigatória da divergência: ");
            justificativa = scanner.nextLine().trim();
        }

        System.out.print("Confirma a conferência e o fechamento do dia? (S/N): ");
        String conf = scanner.nextLine().trim();
        if (!conf.equalsIgnoreCase("s")) {
            System.out.println("Conferência cancelada.");
            pausar();
            return;
        }

        try {
            caixaService.conferirEFechar(contado, justificativa, usuarioLogado);
            System.out.println("\n✅ CAIXA CONFERIDO E FECHADO COM SUCESSO!");
            System.out.println("=== RESUMO DO DIA (RDD) ===");
            System.out.printf("Saldo de Abertura : R$ %,.2f%n", caixa.getSaldoAnterior());
            System.out.printf("Saldo Calculado   : R$ %,.2f%n", saldoEsperado);
            System.out.printf("Valor Contado     : R$ %,.2f%n", contado);
            if (!justificativa.isEmpty()) {
                System.out.println("Justificativa     : " + justificativa);
            }
            System.out.println("Fechado por       : " + usuarioLogado.getNome());

            System.out.print("\nDeseja abrir um novo caixa para o dia seguinte agora com este saldo? (S/N): ");
            String abrirNovo = scanner.nextLine().trim();
            if (abrirNovo.equalsIgnoreCase("s")) {
                repositorio.setCaixaAtual(new Caixa(contado));
                System.out.println("Novo caixa aberto com saldo inicial de R$ " + String.format("%,.2f", contado));
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao fechar caixa: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 5: ABRIR O MÊS SEGUINTE & REAJUSTAR ALUGUEL
    // ========================================================================
    private void fluxoAbrirMesReajuste() {
        System.out.println("--- [MODAL: ABRIR O MÊS & APLICAR REAJUSTES] ---");
        int mes = lerInteiro("Mês da nova competência (1 a 12): ", 1, 12);
        int ano = lerInteiro("Ano da nova competência (ex: 2026): ", 2020, 2035);

        Competencia novaComp = new Competencia(mes, ano);
        Map<Contrato, Double> reajustes = new HashMap<>();

        System.out.print("Deseja aplicar reajuste de aluguel em algum contrato neste novo mês? (S/N): ");
        String aplicarReajuste = scanner.nextLine().trim();
        if (aplicarReajuste.equalsIgnoreCase("s")) {
            List<Contrato> contratos = repositorio.getContratos();
            boolean adicionandoReajustes = true;
            while (adicionandoReajustes) {
                System.out.println("\nContratos disponíveis para reajuste:");
                for (int i = 0; i < contratos.size(); i++) {
                    Contrato c = contratos.get(i);
                    System.out.printf(" [%d] %s (Inquilino: %s | Aluguel atual: R$ %,.2f)%n",
                            i + 1, c.getImovel().getEndereco(), c.getInquilino().getNome(), c.getValorAluguel());
                }
                int idx = lerInteiro("Selecione o contrato para reajustar (ou 0 para concluir seleção): ", 0, contratos.size());
                if (idx == 0) {
                    adicionandoReajustes = false;
                } else {
                    Contrato c = contratos.get(idx - 1);
                    double novoValor = lerDoublePositivo("Novo valor do aluguel para " + c.getImovel().getEndereco() + ": R$ ");
                    reajustes.put(c, novoValor);
                    System.out.println("Reajuste agendado!");
                }
            }
        }

        try {
            competenciaService.abrir(novaComp, reajustes, usuarioLogado);
            System.out.printf("%n✅ Mês %02d/%d aberto com sucesso!%n", mes, ano);
            if (!reajustes.isEmpty()) {
                System.out.println("Reajustes registrados no histórico de contratos (Regra 7):");
                for (Contrato c : reajustes.keySet()) {
                    System.out.println(" • " + c.getImovel().getEndereco() + " -> Novo aluguel: R$ "
                            + String.format("%,.2f", c.getValorAluguel()));
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao abrir mês: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 6: SOLICITAÇÕES DE REPARO
    // ========================================================================
    private void fluxoSolicitacoes() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- [SOLICITAÇÕES DE REPARO NOS IMÓVEIS] ---");
            System.out.println(" [1] Listar Solicitações");
            System.out.println(" [2] Nova Solicitação (Formulário do Modal)");
            System.out.println(" [3] Alterar Status de Solicitação");
            System.out.println(" [0] Voltar");
            System.out.print("Escolha: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> listarSolicitacoes();
                case "2" -> novaSolicitacao();
                case "3" -> alterarStatusSolicitacao();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void listarSolicitacoes() {
        List<Solicitacao> sols = repositorio.getSolicitacoes();
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                           SOLICITAÇÕES DE REPARO                               ");
        System.out.println("--------------------------------------------------------------------------------");
        if (sols.isEmpty()) {
            System.out.println("Nenhuma solicitação cadastrada.");
        } else {
            for (int i = 0; i < sols.size(); i++) {
                Solicitacao s = sols.get(i);
                System.out.printf(" [%d] %s%n", i + 1, s);
                for (String h : s.getHistoricoMudancas()) {
                    System.out.println("      ↳ " + h);
                }
            }
        }
        pausar();
    }

    private void novaSolicitacao() {
        System.out.println("\n--- [MODAL: NOVA SOLICITAÇÃO DE REPARO] ---");
        List<Imovel> imoveis = repositorio.getImoveis();
        if (imoveis.isEmpty()) {
            System.out.println("Nenhum imóvel cadastrado.");
            pausar();
            return;
        }

        System.out.println("Selecione o Imóvel:");
        for (int i = 0; i < imoveis.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, imoveis.get(i).getEndereco());
        }
        int idxIm = lerInteiro("Número do imóvel: ", 1, imoveis.size());
        Imovel imovel = imoveis.get(idxIm - 1);

        System.out.print("O que precisa ser feito (Serviço): ");
        String servico = scanner.nextLine().trim();

        // Filtra apenas prestadores ativos no Supply conforme regra da tela
        List<Prestador> ativos = new ArrayList<>();
        for (Prestador p : repositorio.getPrestadores()) {
            if (p.getStatus() == StatusPrestador.ATIVO) {
                ativos.add(p);
            }
        }
        if (ativos.isEmpty()) {
            System.out.println("Nenhum prestador ativo encontrado no Supply.");
            pausar();
            return;
        }

        System.out.println("\nQuem vai fazer (Cadastros Ativos no Supply):");
        for (int i = 0; i < ativos.size(); i++) {
            Prestador p = ativos.get(i);
            System.out.printf(" [%d] %s (%s - %s)%n", i + 1, p.getNome(), p.getTipo(), p.getServico());
        }
        int idxP = lerInteiro("Número do responsável: ", 1, ativos.size());
        Prestador responsavel = ativos.get(idxP - 1);

        try {
            Solicitacao nova = new Solicitacao(imovel, servico, responsavel);
            repositorio.adicionarSolicitacao(nova);
            System.out.println("✅ Solicitação criada com sucesso! Status inicial: " + nova.getStatus().getDescricao());
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar solicitação: " + e.getMessage());
        }
        pausar();
    }

    private void alterarStatusSolicitacao() {
        List<Solicitacao> sols = repositorio.getSolicitacoes();
        if (sols.isEmpty()) {
            System.out.println("Nenhuma solicitação cadastrada.");
            pausar();
            return;
        }

        System.out.println("\nSelecione a Solicitação para alterar status:");
        for (int i = 0; i < sols.size(); i++) {
            System.out.printf(" [%d] %s%n", i + 1, sols.get(i));
        }
        int idx = lerInteiro("Número da solicitação: ", 1, sols.size());
        Solicitacao sol = sols.get(idx - 1);

        System.out.println("\nEscolha o novo status:");
        System.out.println(" [1] na fila");
        System.out.println(" [2] em andamento");
        System.out.println(" [3] bloqueado");
        System.out.println(" [4] finalizado");
        int op = lerInteiro("Opção: ", 1, 4);
        StatusSolicitacao novoStatus = switch (op) {
            case 1 -> StatusSolicitacao.NA_FILA;
            case 2 -> StatusSolicitacao.EM_ANDAMENTO;
            case 3 -> StatusSolicitacao.BLOQUEADO;
            default -> StatusSolicitacao.FINALIZADO;
        };

        try {
            sol.mudarStatus(novoStatus, usuarioLogado);
            System.out.println("✅ Status atualizado e registrado no histórico!");
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 7: SUPPLY (FUNCIONÁRIOS E PRESTADORES)
    // ========================================================================
    private void fluxoSupply() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- [SUPPLY: FUNCIONÁRIOS E TERCEIRIZADOS] ---");
            System.out.println(" [1] Listar Cadastros do Supply");
            System.out.println(" [2] Cadastrar Novo Prestador/Funcionário (Formulário)");
            System.out.println(" [3] Alterar Status de Prestador (Ativo / Inativo / Bloqueado)");
            System.out.println(" [0] Voltar");
            System.out.print("Escolha: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> listarSupply();
                case "2" -> novoPrestadorSupply();
                case "3" -> alterarStatusSupply();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void listarSupply() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                    CADASTROS DO SUPPLY (PRESTADORES E EQUIPE)                  ");
        System.out.println("--------------------------------------------------------------------------------");
        List<Prestador> prestadores = repositorio.getPrestadores();
        for (int i = 0; i < prestadores.size(); i++) {
            Prestador p = prestadores.get(i);
            System.out.printf(" [%d] %-28s | %-7s | %-12s | %-25s | Doc: %-18s | [%s]%n",
                    i + 1, p.getNome(), p.getTipo(), p.getVinculo(), p.getServico(),
                    p.getDocumento(), p.getStatus().getDescricao());
        }
        pausar();
    }

    private void novoPrestadorSupply() {
        System.out.println("\n--- [MODAL: CADASTRAR NO SUPPLY] ---");
        System.out.print("Nome / Razão Social: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Documento (CPF com 11 dígitos ou CNPJ com 14 dígitos): ");
        String doc = scanner.nextLine().trim();

        System.out.println("Tipo: [1] Pessoa  [2] Empresa");
        int t = lerInteiro("Escolha (1 ou 2): ", 1, 2);
        String tipo = (t == 1) ? "Pessoa" : "Empresa";

        System.out.println("Vínculo: [1] Direto  [2] Terceirizado");
        int v = lerInteiro("Escolha (1 ou 2): ", 1, 2);
        String vinculo = (v == 1) ? "Direto" : "Terceirizado";

        System.out.print("Serviço / Atuação: ");
        String servico = scanner.nextLine().trim();

        try {
            Prestador novo = new Prestador(nome, doc, tipo, vinculo, servico, StatusPrestador.ATIVO);
            repositorio.adicionarPrestador(novo);
            System.out.println("✅ Prestador cadastrado com sucesso no Supply!");
        } catch (Exception e) {
            System.out.println("❌ Erro ao cadastrar prestador: " + e.getMessage());
        }
        pausar();
    }

    private void alterarStatusSupply() {
        List<Prestador> prestadores = repositorio.getPrestadores();
        System.out.println("\nSelecione o prestador:");
        for (int i = 0; i < prestadores.size(); i++) {
            System.out.printf(" [%d] %s [%s]%n", i + 1, prestadores.get(i).getNome(),
                    prestadores.get(i).getStatus().getDescricao());
        }
        int idx = lerInteiro("Número do prestador: ", 1, prestadores.size());
        Prestador p = prestadores.get(idx - 1);

        System.out.println("Novo status: [1] Ativo  [2] Inativo  [3] Bloqueado");
        int st = lerInteiro("Escolha (1-3): ", 1, 3);
        StatusPrestador novo = switch (st) {
            case 1 -> StatusPrestador.ATIVO;
            case 2 -> StatusPrestador.INATIVO;
            default -> StatusPrestador.BLOQUEADO;
        };

        p.setStatus(novo);
        System.out.println("✅ Status atualizado com sucesso!");
        pausar();
    }

    // ========================================================================
    // FLUXO 8: IMÓVEIS, INQUILINOS & CONTRATOS
    // ========================================================================
    private void fluxoImoveisInquilinosContratos() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- [IMÓVEIS, INQUILINOS E CONTRATOS DE LOCAÇÃO] ---");
            System.out.println(" [1] Listar Imóveis");
            System.out.println(" [2] Cadastrar Novo Imóvel (Apartamento, Casa, Loja, Sala, Terreno)");
            System.out.println(" [3] Listar Inquilinos");
            System.out.println(" [4] Cadastrar Novo Inquilino");
            System.out.println(" [5] Listar Contratos de Locação");
            System.out.println(" [6] Criar Novo Contrato de Locação");
            System.out.println(" [0] Voltar");
            System.out.print("Escolha: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> listarImoveis();
                case "2" -> cadastrarImovel();
                case "3" -> listarInquilinos();
                case "4" -> cadastrarInquilino();
                case "5" -> listarContratos();
                case "6" -> cadastrarContrato();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void listarImoveis() {
        System.out.println("\n--- [LISTA DE IMÓVEIS DA FUNDAÇÃO] ---");
        List<Imovel> lista = repositorio.getImoveis();
        for (int i = 0; i < lista.size(); i++) {
            Imovel im = lista.get(i);
            System.out.printf(" [%02d] %-30s | Tipo: %-11s | Situação: %-10s | Pode alugar? %s%n",
                    i + 1, im.getEndereco(), im.getTipo(), im.getSituacao(), im.podeSerAlugado() ? "Sim" : "Não");
        }
        pausar();
    }

    private void cadastrarImovel() {
        System.out.println("\n--- [MODAL: REGISTRAR IMÓVEL] ---");
        System.out.println("Tipo de Imóvel:");
        System.out.println(" [1] Apartamento");
        System.out.println(" [2] Casa");
        System.out.println(" [3] Loja");
        System.out.println(" [4] Sala");
        System.out.println(" [5] Terreno");
        int t = lerInteiro("Escolha o tipo (1-5): ", 1, 5);

        System.out.print("Endereço do imóvel: ");
        String end = scanner.nextLine().trim();

        Imovel novo;
        try {
            switch (t) {
                case 1 -> {
                    System.out.print("Nome do Edifício: ");
                    String ed = scanner.nextLine().trim();
                    System.out.print("Número do Apartamento: ");
                    String num = scanner.nextLine().trim();
                    novo = new Apartamento(end, "vago", ed, num);
                }
                case 2 -> novo = new Casa(end, "vago");
                case 3 -> novo = new Loja(end, "vago");
                case 4 -> novo = new Sala(end, "vago");
                default -> novo = new Terreno(end, "vago");
            }
            repositorio.adicionarImovel(novo);
            System.out.println("✅ Imóvel cadastrado com sucesso! (" + novo.getTipo() + ")");
        } catch (Exception e) {
            System.out.println("❌ Erro ao cadastrar imóvel: " + e.getMessage());
        }
        pausar();
    }

    private void listarInquilinos() {
        System.out.println("\n--- [LISTA DE INQUILINOS] ---");
        List<Inquilino> lista = repositorio.getInquilinos();
        for (int i = 0; i < lista.size(); i++) {
            Inquilino inq = lista.get(i);
            System.out.printf(" [%02d] %-25s | CPF: %-14s | Tel: %s%n",
                    i + 1, inq.getNome(), inq.getCpf(), inq.getTelefone());
        }
        pausar();
    }

    private void cadastrarInquilino() {
        System.out.println("\n--- [CADASTRAR NOVO INQUILINO] ---");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();
        System.out.print("CPF (11 dígitos): ");
        String cpf = scanner.nextLine().trim();
        System.out.print("Telefone: ");
        String tel = scanner.nextLine().trim();

        try {
            Inquilino novo = new Inquilino(nome, cpf, tel);
            repositorio.adicionarInquilino(novo);
            System.out.println("✅ Inquilino cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro ao cadastrar inquilino: " + e.getMessage());
        }
        pausar();
    }

    private void listarContratos() {
        System.out.println("\n--- [CONTRATOS DE LOCAÇÃO ATIVOS] ---");
        List<Contrato> lista = repositorio.getContratos();
        for (int i = 0; i < lista.size(); i++) {
            Contrato c = lista.get(i);
            String vencidoStr = c.estaVencido() ? " [VENCIDO]" : "";
            System.out.printf(" [%02d] %-30s | Inq: %-16s | R$ %,.2f | Vence todo dia: %02d | Até: %s%s%n",
                    i + 1, c.getImovel().getEndereco(), c.getInquilino().getNome(),
                    c.getValorAluguel(), c.getDiaVencimento(), c.getVigenteAte().format(FORMATO_DATA), vencidoStr);
        }
        pausar();
    }

    private void cadastrarContrato() {
        System.out.println("\n--- [CRIAR NOVO CONTRATO DE LOCAÇÃO] ---");
        List<Imovel> vagos = new ArrayList<>();
        for (Imovel im : repositorio.getImoveis()) {
            if (im.podeSerAlugado()) {
                vagos.add(im);
            }
        }
        if (vagos.isEmpty()) {
            System.out.println("Nenhum imóvel disponível para locação no momento.");
            pausar();
            return;
        }

        System.out.println("Selecione o Imóvel vago:");
        for (int i = 0; i < vagos.size(); i++) {
            System.out.printf(" [%d] %s (%s)%n", i + 1, vagos.get(i).getEndereco(), vagos.get(i).getTipo());
        }
        int idxIm = lerInteiro("Escolha o imóvel: ", 1, vagos.size());
        Imovel imovel = vagos.get(idxIm - 1);

        List<Inquilino> inqs = repositorio.getInquilinos();
        System.out.println("\nSelecione o Inquilino:");
        for (int i = 0; i < inqs.size(); i++) {
            System.out.printf(" [%d] %s (CPF: %s)%n", i + 1, inqs.get(i).getNome(), inqs.get(i).getCpf());
        }
        int idxInq = lerInteiro("Escolha o inquilino: ", 1, inqs.size());
        Inquilino inquilino = inqs.get(idxInq - 1);

        double aluguel = lerDoublePositivo("Valor mensal do aluguel (R$): ");
        int diaVenc = lerInteiro("Dia de vencimento (1 a 28, conforme regra): ", 1, 28);
        int mesesVigencia = lerInteiro("Vigência do contrato em meses (ex: 12 ou 24): ", 1, 60);
        LocalDate vigenteAte = LocalDate.now().plusMonths(mesesVigencia);

        try {
            Contrato contrato = new Contrato(imovel, inquilino, aluguel, diaVenc, vigenteAte);
            repositorio.adicionarContrato(contrato);
            System.out.println("✅ Contrato de locação criado com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar contrato: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // FLUXO 9: RECIBOS & AUDITORIA
    // ========================================================================
    private void fluxoRecibosAuditoria() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- [RECIBOS, PRESTAÇÃO DE CONTAS & AUDITORIA] ---");
            System.out.println(" [1] Listar Recibos Emitidos");
            System.out.println(" [2] Imprimir Recibo Específico (Imprimivel)");
            System.out.println(" [3] Testar Imutabilidade do Recibo (Regra 1 - ReciboImutavelException)");
            System.out.println(" [4] Trilha Completa de Auditoria (Auditavel)");
            System.out.println(" [5] Posição Financeira Consolidada");
            System.out.println(" [0] Voltar");
            System.out.print("Escolha: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> listarRecibos();
                case "2" -> imprimirRecibo();
                case "3" -> testarImutabilidadeRecibo();
                case "4" -> listarAuditoria();
                case "5" -> posicaoConsolidada();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void listarRecibos() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                               RECIBOS EMITIDOS                                 ");
        System.out.println("--------------------------------------------------------------------------------");
        List<Recibo> lista = repositorio.getRecibos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum recibo emitido até o momento.");
        } else {
            for (int i = 0; i < lista.size(); i++) {
                Recibo r = lista.get(i);
                System.out.printf(" [%02d] Nº %-9s | %-28s | Inq: %-15s | R$ %,.2f | Forma: %-11s | Emitido por: %s%n",
                        i + 1, r.getNumero(), r.getContrato().getImovel().getEndereco(),
                        r.getContrato().getInquilino().getNome(), r.getValorTotal(),
                        r.getForma().getNome(), r.getEmitidoPor().getNome());
            }
        }
        pausar();
    }

    private void imprimirRecibo() {
        List<Recibo> lista = repositorio.getRecibos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum recibo emitido.");
            pausar();
            return;
        }
        listarRecibos();
        int idx = lerInteiro("Selecione o recibo para gerar impressão: ", 1, lista.size());
        System.out.println("\n" + lista.get(idx - 1).gerarDocumento());
        pausar();
    }

    private void testarImutabilidadeRecibo() {
        List<Recibo> lista = repositorio.getRecibos();
        if (lista.isEmpty()) {
            System.out.println("Emita ao menos um recibo no Fluxo 1 antes de executar este teste.");
            pausar();
            return;
        }
        Recibo r = lista.get(0);
        System.out.println("\nTestando alteração no recibo Nº " + r.getNumero() + "...");
        try {
            r.alterarReciboEmitido();
        } catch (ReciboImutavelException e) {
            System.out.println("🛡️ [PROTEÇÃO CONFIRMADA]: " + e.getMessage());
            System.out.println("Regra 1 comprovada: recibos não se editam, correções devem ser feitas com Estorno!");
        }
        pausar();
    }

    private void listarAuditoria() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                   TRILHA DE AUDITORIA (QUEM FEZ E QUANDO)                      ");
        System.out.println("--------------------------------------------------------------------------------");
        List<Auditavel> trilha = repositorio.getHistoricoAuditoria();
        if (trilha.isEmpty()) {
            System.out.println("Nenhum registro de auditoria arquivado.");
        } else {
            for (int i = 0; i < trilha.size(); i++) {
                Auditavel a = trilha.get(i);
                System.out.printf(" [%02d] Em %s por %s: %s%n",
                        i + 1, a.getDataHoraAcao().withNano(0),
                        a.getResponsavelAcao().getNome(), a);
            }
        }
        pausar();
    }

    private void posicaoConsolidada() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.println("                        POSIÇÃO FINANCEIRA CONSOLIDADA                          ");
        System.out.println("--------------------------------------------------------------------------------");
        Caixa caixa = repositorio.getCaixaAtual();
        double saldoCaixa = caixa.saldo();
        double saldoBanco = repositorio.getSaldoBanco();
        double total = saldoCaixa + saldoBanco;

        System.out.printf("Disponível em Caixa Físico : R$ %,.2f [%s]%n", saldoCaixa, caixa.isFechado() ? "Fechado" : "Aberto");
        System.out.printf("Disponível em Contas Banco : R$ %,.2f%n", saldoBanco);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("TOTAL GERAL DISPONÍVEL     : R$ %,.2f%n", total);
        pausar();
    }

    // ========================================================================
    // FLUXO 10: USUÁRIOS & ACESSO
    // ========================================================================
    private void fluxoUsuariosAcesso() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n--- [USUÁRIOS E CONTROLE DE ACESSO] ---");
            System.out.println(" [1] Alternar Usuário Ativo da Sessão");
            System.out.println(" [2] Fazer Login (E-mail ou CPF + Senha)");
            System.out.println(" [3] Cadastrar Novo Usuário com Código de Convite (Regra 10)");
            System.out.println(" [4] Gerar Novo Código de Convite (Apenas Gestor)");
            System.out.println(" [0] Voltar");
            System.out.print("Escolha: ");
            String op = scanner.nextLine().trim();

            switch (op) {
                case "1" -> alternarUsuario();
                case "2" -> fazerLogin();
                case "3" -> cadastrarComConvite();
                case "4" -> gerarConvite();
                case "0" -> voltar = true;
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private void alternarUsuario() {
        List<Usuario> users = repositorio.getUsuarios();
        System.out.println("\nSelecione o usuário para alternar a sessão:");
        for (int i = 0; i < users.size(); i++) {
            Usuario u = users.get(i);
            String perfil = (u instanceof kosmos.modelo.Gestor) ? "Gestor" : "Funcionária";
            String atual = (u == usuarioLogado) ? " (ATUAL)" : "";
            System.out.printf(" [%d] %s - %s%s%n", i + 1, u.getNome(), perfil, atual);
        }
        int idx = lerInteiro("Escolha: ", 1, users.size());
        this.usuarioLogado = users.get(idx - 1);
        System.out.println("✅ Sessão alterada para: " + usuarioLogado.getNome());
        pausar();
    }

    private void fazerLogin() {
        System.out.println("\n--- [LOGIN NO KOSMOS] ---");
        System.out.print("E-mail ou CPF: ");
        String login = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String senha = scanner.nextLine().trim();

        try {
            Usuario u = authService.entrar(login, senha);
            this.usuarioLogado = u;
            System.out.println("✅ Login realizado com sucesso! Bem-vindo(a), " + u.getNome());
        } catch (Exception e) {
            System.out.println("❌ Falha na autenticação: " + e.getMessage());
        }
        pausar();
    }

    private void cadastrarComConvite() {
        System.out.println("\n--- [MODAL: CRIAR CONTA COM CÓDIGO DE CONVITE] ---");
        System.out.println("Códigos de convite disponíveis no sistema:");
        for (Convite c : repositorio.getConvites()) {
            if (!c.isUsado()) {
                System.out.println(" • " + c.getCodigo() + " (Função: " + c.getFuncao() + ")");
            }
        }

        System.out.print("\nDigite o Código de Convite: ");
        String cod = scanner.nextLine().trim();
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine().trim();
        System.out.print("CPF (11 dígitos): ");
        String cpf = scanner.nextLine().trim();
        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Senha (mínimo 6 caracteres): ");
        String senha = scanner.nextLine().trim();

        try {
            Usuario novo = authService.cadastrar(cod, nome, cpf, email, senha);
            System.out.println("✅ Conta criada com sucesso para " + novo.getNome() + "!");
            System.out.print("Deseja alternar a sessão para este novo usuário agora? (S/N): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                this.usuarioLogado = novo;
            }
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar conta: " + e.getMessage());
        }
        pausar();
    }

    private void gerarConvite() {
        if (!usuarioLogado.pode(Acao.GERENCIAR_PESSOAS)) {
            System.out.println("❌ Permissão negada: apenas o Gestor pode gerar códigos de convite.");
            pausar();
            return;
        }

        System.out.println("\n--- [GERAR CÓDIGO DE CONVITE] ---");
        System.out.print("Código desejado (ex: KSM-FUNC-02): ");
        String cod = scanner.nextLine().trim();
        System.out.println("Função do convite: [1] GESTOR  [2] FUNCIONARIO");
        int f = lerInteiro("Escolha (1 ou 2): ", 1, 2);
        String funcao = (f == 1) ? "GESTOR" : "FUNCIONARIO";

        try {
            Convite c = new Convite(cod, funcao);
            repositorio.adicionarConvite(c);
            System.out.println("✅ Código de convite '" + cod + "' gerado com sucesso para a função " + funcao);
        } catch (Exception e) {
            System.out.println("❌ Erro: " + e.getMessage());
        }
        pausar();
    }

    // ========================================================================
    // MÉTODOS UTILITÁRIOS DE LEITURA
    // ========================================================================
    private int lerInteiro(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String l = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(l);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("Valor deve estar entre %d e %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    private double lerDoublePositivo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String l = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(l);
                if (valor > 0) {
                    return valor;
                }
                System.out.println("O valor deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número válido (ex: 1250.50).");
            }
        }
    }

    private LocalDate lerData(String prompt, LocalDate padrao) {
        while (true) {
            System.out.print(prompt);
            String l = scanner.nextLine().trim();
            if (l.isEmpty()) {
                return padrao;
            }
            try {
                return LocalDate.parse(l, FORMATO_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use DD/MM/AAAA (ex: 10/09/2026).");
            }
        }
    }

    private void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }
}
