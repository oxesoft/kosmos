# Fluxo Operacional e de Regras de Negócio · Kosmos

Diagrama de fluxo Mermaid que ilustra a navegação geral, a validação de permissões de acesso e os fluxos operacionais da plataforma **Kosmos** (gestão de locações e caixa da Fundação Manuel Cruz), exercitando os pilares de Programação Orientada a Objetos e as regras de negócio do sistema.

```mermaid
flowchart TD
    %% Início e Autenticação
    START(["Início / Inicialização"]) --> LOGIN_CHECK{"Usuário autenticado?"}
    LOGIN_CHECK -- Não --> AUTH_CHOICE{"Possui conta?"}
    AUTH_CHOICE -- Sim --> FORM_LOGIN["Formulário de Login\n(E-mail ou CPF + Senha)"]
    AUTH_CHOICE -- Não --> FORM_CADASTRO["Cadastro com Convite\n(Código válido e não usado - Regra 10)"]
    FORM_LOGIN --> VALIDA_AUTH{"Credenciais válidas?"}
    FORM_CADASTRO --> VALIDA_AUTH
    VALIDA_AUTH -- Não --> AUTH_CHOICE
    VALIDA_AUTH -- Sim --> MENU

    LOGIN_CHECK -- Sim --> MENU["Menu Principal\n(Sessão do Usuário: Gestor ou Funcionário)"]

    %% Opções do Menu Principal
    MENU --> OP1["[1] Registrar Recebimento"]
    MENU --> OP2["[2] Busca Unificada"]
    MENU --> OP3["[3] Movimento de Caixa"]
    MENU --> OP4["[4] Conferir e Fechar o Dia"]
    MENU --> OP5["[5] Abrir Mês e Reajustar"]
    MENU --> OP6["[6] Solicitações de Reparo"]
    MENU --> OP7["[7] Gestão do Supply"]
    MENU --> OP8["[8] Imóveis, Inquilinos e Contratos"]
    MENU --> OP9["[9] Recibos e Auditoria"]
    MENU --> OP10["[10] Usuários e Acesso"]
    MENU --> OP0(["[0] Sair"])

    %% Fluxo 1: Recebimento de Aluguel
    OP1 --> SEL_CONTRATO["Selecionar Contrato Ativo"]
    SEL_CONTRATO --> SEL_COMPS["Selecionar Competência(s)"]
    SEL_COMPS --> CHECK_ATRASO{"Pagamento após o vencimento?"}
    CHECK_ATRASO -- Sim --> CALC_ENCARGOS["Calcular Encargos Automaticamente\n(2% multa + 0,033% ao dia - Regra 3)"]
    CHECK_ATRASO -- Não --> FORMA_PAGTO
    CALC_ENCARGOS --> FORMA_PAGTO["Selecionar Forma de Pagamento"]

    FORMA_PAGTO --> DESTINO_PAGTO{"Polimorfismo:\nFormaPagamento.destino()"}
    DESTINO_PAGTO -- "Dinheiro (CAIXA)" --> GERA_LANC_CAIXA["Gera LancamentoDeRecibo\nno Caixa do Dia - Regra 4"]
    DESTINO_PAGTO -- "PIX / Transf. / Depósito (BANCO)" --> GERA_CRED_BANCO["Credita saldo na conta bancária"]

    GERA_LANC_CAIXA --> EMITE_RECIBO["Emite Recibo(s) Imutável(is)\n(1 por mês - Regras 1 e 2)"]
    GERA_CRED_BANCO --> EMITE_RECIBO
    EMITE_RECIBO --> MENU

    %% Fluxo 3: Movimento de Caixa
    OP3 --> CAIXA_MENU{"Operação no Caixa"}
    CAIXA_MENU -- "Extrato" --> VER_EXTRATO["Exibe lançamentos, entradas, saídas\ne saldo progressivo"]
    CAIXA_MENU -- "Novo Lançamento Manual" --> CHECK_CAIXA_ABERTO{"Caixa do dia fechado?"}
    CHECK_CAIXA_ABERTO -- Sim --> ERR_DIA_FECHADO["Bloqueia operação:\nDiaFechadoException - Regra 5"]
    CHECK_CAIXA_ABERTO -- Não --> LANC_MANUAL["Registra LancamentoManual\n(Entrada ou Saída)"]
    CAIXA_MENU -- "Estorno" --> CHECK_GESTOR{"pode(Acao.ESTORNAR)?\nPolimorfismo - Regra 9"}
    CHECK_GESTOR -- Não (Funcionário) --> ERR_PERMISSAO["Permissão negada:\nSomente Gestor pode estornar"]
    CHECK_GESTOR -- Sim (Gestor) --> EXEC_ESTORNO["Exige justificativa e gera Estorno\ninvertendo o valor - Regras 6 e 8"]

    VER_EXTRATO --> MENU
    LANC_MANUAL --> MENU
    ERR_DIA_FECHADO --> MENU
    ERR_PERMISSAO --> MENU
    EXEC_ESTORNO --> MENU

    %% Fluxo 4: Conferência e Fechamento
    OP4 --> COMPARA_SALDO{"Valor físico contado == Saldo calculado?"}
    COMPARA_SALDO -- Não (Divergência) --> EXIGE_JUST["Exige justificativa obrigatória\n(Regra 6)"]
    COMPARA_SALDO -- Sim --> FECHA_DIA
    EXIGE_JUST --> FECHA_DIA["Fecha o Caixa e gera Resumo do Dia (RDD)"]
    FECHA_DIA --> NOVO_DIA_OPT{"Abrir caixa do dia seguinte?"}
    NOVO_DIA_OPT -- Sim --> NOVO_CAIXA["Abre novo Caixa com saldo inicial apurado"]
    NOVO_DIA_OPT -- Não --> MENU
    NOVO_CAIXA --> MENU

    %% Fluxo 5: Abrir Mês e Reajuste
    OP5 --> CRIA_COMP["Abre Nova Competência (Mês/Ano)"]
    CRIA_COMP --> REAJUSTE_OPT{"Aplicar reajustes?"}
    REAJUSTE_OPT -- Sim --> APLICA_REAJUSTE["Contrato.reajustar(...)\nRegistra histórico do valor antigo e novo - Regra 7"]
    REAJUSTE_OPT -- Não --> MENU
    APLICA_REAJUSTE --> MENU

    %% Fluxo 6: Solicitações
    OP6 --> SOLIC_OP{"Operação de Solicitação"}
    SOLIC_OP -- "Nova Solicitação" --> SEL_PRESTADOR["Seleciona Prestador Ativo no Supply\n(Status inicial: NA_FILA)"]
    SOLIC_OP -- "Mudar Status" --> MUDA_STATUS["mudarStatus(...)\nRegistra data, hora e operador na auditoria"]
    SEL_PRESTADOR --> MENU
    MUDA_STATUS --> MENU

    %% Outros Fluxos
    OP2 --> BUSCA_EXEC["Busca em Imóveis, Inquilinos e Recibos"] --> MENU
    OP7 --> SUPPLY_EXEC["Cadastra Prestador (validação CPF/CNPJ) ou altera status"] --> MENU
    OP8 --> CAD_EXEC["Cadastra Imóvel, Inquilino ou Contrato"] --> MENU
    OP9 --> AUDIT_EXEC["Exibe Recibos, Trilha de Auditoria e Posição Consolidada"] --> MENU
    OP10 --> SESSAO_EXEC["Alterna usuário logado, gera convites ou efetua login"] --> MENU
```
