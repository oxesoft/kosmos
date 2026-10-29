package kosmos.repositorio;

import java.time.LocalDate;
import kosmos.modelo.Apartamento;
import kosmos.modelo.Caixa;
import kosmos.modelo.Casa;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.Convite;
import kosmos.modelo.Imovel;
import kosmos.modelo.Inquilino;
import kosmos.modelo.LancamentoManual;
import kosmos.modelo.Loja;
import kosmos.modelo.Prestador;
import kosmos.modelo.Sala;
import kosmos.modelo.Solicitacao;
import kosmos.modelo.StatusPrestador;
import kosmos.modelo.StatusSolicitacao;
import kosmos.modelo.Terreno;
import kosmos.modelo.TipoMovimento;
import kosmos.modelo.Usuario;
import kosmos.servico.AutenticacaoService;

/**
 * Carrega a base inicial de dados em memória, espelhando fielmente os dados
 * das telas do Kosmos e do README.md.
 */
public class DadosIniciais {

    public static Usuario carregarDados(KosmosRepositorio repo, AutenticacaoService auth) {
        // 1. Convites e Usuários Iniciais
        Convite c1 = new Convite("KSM-GESTOR-01", "GESTOR");
        Convite c2 = new Convite("KSM-FUNC-01", "FUNCIONARIO");
        Convite c3 = new Convite("KSM-GESTOR-99", "GESTOR");
        Convite c4 = new Convite("KSM-FUNC-99", "FUNCIONARIO");
        repo.adicionarConvite(c1);
        repo.adicionarConvite(c2);
        repo.adicionarConvite(c3);
        repo.adicionarConvite(c4);

        Usuario gestorDavi = auth.cadastrar(
                "KSM-GESTOR-01", "Davi Nogueira", "111.222.333-44", "davi@kosmos.org.br", "senha123");
        Usuario funcAna = auth.cadastrar(
                "KSM-FUNC-01", "Ana Beatriz Lins", "234.567.890-11", "ana@kosmos.org.br", "senha456");

        // 2. Caixa Inicial (quinta, 10 de setembro de 2026 - tela 03_caixa.png)
        Caixa caixa = new Caixa(3180.00); // Saldo anterior: R$ 3.180,00
        repo.setCaixaAtual(caixa);

        caixa.lancar(new LancamentoManual(1310.00, TipoMovimento.ENTRADA,
                "Aluguel set/26 · Ed. São Jorge · apto 304", gestorDavi));
        caixa.lancar(new LancamentoManual(870.00, TipoMovimento.ENTRADA,
                "Aluguel set/26 · Rua Coronel Bento, 20", funcAna));
        caixa.lancar(new LancamentoManual(340.00, TipoMovimento.SAIDA,
                "Material de limpeza · residencial", gestorDavi));
        caixa.lancar(new LancamentoManual(700.00, TipoMovimento.SAIDA,
                "Depósito bancário", gestorDavi));
        repo.creditarNoBanco(187540.00);

        // 3. Imóveis
        Imovel ap101 = new Apartamento("Ed. São Jorge · apto 101", "vago", "Ed. São Jorge", "101");
        Imovel ap203 = new Apartamento("Ed. São Jorge · apto 203", "vago", "Ed. São Jorge", "203");
        Imovel ap304 = new Apartamento("Ed. São Jorge · apto 304", "vago", "Ed. São Jorge", "304");

        Imovel lj1 = new Loja("Ed. Santo Antônio · loja 1", "vago");
        Imovel lj2 = new Loja("Ed. Santo Antônio · loja 2", "vago");
        Imovel sl5 = new Sala("Ed. Santo Antônio · sala 5", "vago");

        Imovel cs14 = new Casa("Rua Coronel Bento, 14", "vago");
        Imovel cs20 = new Casa("Rua Coronel Bento, 20", "vago");
        Imovel cs88 = new Casa("Rua das Palmeiras, 88", "vago");
        Imovel cs92 = new Casa("Rua das Palmeiras, 92", "vago");
        Imovel cs96 = new Casa("Rua das Palmeiras, 96", "em reforma");

        Imovel tr03 = new Terreno("Loteamento Norte, quadra 3", "sem uso");

        repo.adicionarImovel(ap101);
        repo.adicionarImovel(ap203);
        repo.adicionarImovel(ap304);
        repo.adicionarImovel(lj1);
        repo.adicionarImovel(lj2);
        repo.adicionarImovel(sl5);
        repo.adicionarImovel(cs14);
        repo.adicionarImovel(cs20);
        repo.adicionarImovel(cs88);
        repo.adicionarImovel(cs92);
        repo.adicionarImovel(cs96);
        repo.adicionarImovel(tr03);

        // 4. Inquilinos
        Inquilino inqMarta = new Inquilino("Marta Ribeiro", "111.222.333-01", "(85) 98888-0001");
        Inquilino inqCelio = new Inquilino("Célio Amorim", "222.333.444-02", "(85) 98888-0002");
        Inquilino inqHamilton = new Inquilino("Hamilton Régis", "333.444.555-03", "(85) 98888-0003");
        Inquilino inqIvan = new Inquilino("Ivan Serpa", "444.555.666-04", "(85) 98888-0004");
        Inquilino inqAurelio = new Inquilino("Aurélio Tavares", "555.666.777-05", "(85) 98888-0005");
        Inquilino inqOtavio = new Inquilino("Otávio Braga", "666.777.888-06", "(85) 98888-0006");
        Inquilino inqNilza = new Inquilino("Nilza Peixoto", "777.888.999-07", "(85) 98888-0007");
        Inquilino inqElza = new Inquilino("Elza Fontes", "888.999.000-08", "(85) 98888-0008");
        Inquilino inqVania = new Inquilino("Vânia Sobral", "999.000.111-09", "(85) 98888-0009");
        Inquilino inqSonia = new Inquilino("Sônia Vilar", "101.202.303-10", "(85) 98888-0010");

        repo.adicionarInquilino(inqMarta);
        repo.adicionarInquilino(inqCelio);
        repo.adicionarInquilino(inqHamilton);
        repo.adicionarInquilino(inqIvan);
        repo.adicionarInquilino(inqAurelio);
        repo.adicionarInquilino(inqOtavio);
        repo.adicionarInquilino(inqNilza);
        repo.adicionarInquilino(inqElza);
        repo.adicionarInquilino(inqVania);
        repo.adicionarInquilino(inqSonia);

        // 5. Contratos de Locação
        repo.adicionarContrato(new Contrato(ap101, inqMarta, 1240.00, 5, LocalDate.now().plusMonths(12)));
        repo.adicionarContrato(new Contrato(ap203, inqCelio, 1180.00, 5, LocalDate.now().plusMonths(6)));
        repo.adicionarContrato(new Contrato(ap304, inqHamilton, 1310.00, 5, LocalDate.now().plusMonths(18)));
        repo.adicionarContrato(new Contrato(lj1, inqIvan, 3100.00, 10, LocalDate.now().plusMonths(24)));
        repo.adicionarContrato(new Contrato(lj2, inqAurelio, 2300.00, 10, LocalDate.now().plusMonths(15)));
        repo.adicionarContrato(new Contrato(sl5, inqOtavio, 740.00, 10, LocalDate.now().plusMonths(8)));
        repo.adicionarContrato(new Contrato(cs14, inqNilza, 1050.00, 5, LocalDate.now().plusMonths(10)));
        repo.adicionarContrato(new Contrato(cs20, inqElza, 870.00, 5, LocalDate.now().plusMonths(11)));
        repo.adicionarContrato(new Contrato(cs88, inqVania, 960.00, 10, LocalDate.now().plusMonths(14)));
        repo.adicionarContrato(new Contrato(cs92, inqSonia, 910.00, 10, LocalDate.now().plusMonths(7)));

        // 6. Competências
        Competencia comp08 = new Competencia(8, 2026);
        Competencia comp09 = new Competencia(9, 2026);
        Competencia comp10 = new Competencia(10, 2026);
        repo.adicionarCompetencia(comp08);
        repo.adicionarCompetencia(comp09);
        repo.adicionarCompetencia(comp10);

        // 7. Supply (Prestadores)
        Prestador p1 = new Prestador("Reforma Fácil Serviços Ltda", "12.345.678/0001-90",
                "Empresa", "Terceirizado", "Reparos e pequenas obras", StatusPrestador.ATIVO);
        Prestador p2 = new Prestador("Hidro Norte Instalações", "23.456.789/0001-10",
                "Empresa", "Terceirizado", "Hidráulica", StatusPrestador.ATIVO);
        Prestador p3 = new Prestador("Eletro Silva ME", "34.567.890/0001-21",
                "Empresa", "Terceirizado", "Elétrica", StatusPrestador.INATIVO);
        Prestador p4 = new Prestador("Jardins do Sertão", "45.678.901/0001-32",
                "Empresa", "Terceirizado", "Jardinagem", StatusPrestador.INATIVO);
        Prestador p5 = new Prestador("José Ribamar Costa", "123.456.789-00",
                "Pessoa", "Direto", "Zeladoria · Ed. São Jorge", StatusPrestador.ATIVO);
        Prestador p6 = new Prestador("Ana Beatriz Lins", "234.567.890-11",
                "Pessoa", "Direto", "Administrativo", StatusPrestador.ATIVO);
        Prestador p7 = new Prestador("Francisca Almeida", "345.678.901-22",
                "Pessoa", "Terceirizado", "Limpeza · Ed. Santo Antônio", StatusPrestador.ATIVO);

        repo.adicionarPrestador(p1);
        repo.adicionarPrestador(p2);
        repo.adicionarPrestador(p3);
        repo.adicionarPrestador(p4);
        repo.adicionarPrestador(p5);
        repo.adicionarPrestador(p6);
        repo.adicionarPrestador(p7);

        // 8. Solicitações de Reparo
        Solicitacao s1 = new Solicitacao(cs88, "Troca da fechadura do portão", p1);
        Solicitacao s2 = new Solicitacao(cs14, "Vazamento no banheiro", p2);
        s2.mudarStatus(StatusSolicitacao.EM_ANDAMENTO, gestorDavi);
        Solicitacao s3 = new Solicitacao(ap203, "Tomada da cozinha sem energia", p1);
        s3.mudarStatus(StatusSolicitacao.BLOQUEADO, gestorDavi);
        Solicitacao s4 = new Solicitacao(cs96, "Reforma geral do imóvel", p1);
        s4.mudarStatus(StatusSolicitacao.EM_ANDAMENTO, gestorDavi);

        repo.adicionarSolicitacao(s1);
        repo.adicionarSolicitacao(s2);
        repo.adicionarSolicitacao(s3);
        repo.adicionarSolicitacao(s4);

        return gestorDavi; // Usuário logado por padrão
    }
}
