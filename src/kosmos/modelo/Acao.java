package kosmos.modelo;

/**
 * Enumeração das ações do sistema sujeitas a controle de permissão polimórfico em Usuario.pode(Acao).
 * Regra 9: Funcionário não estorna, não fecha exercício e não gerencia pessoas.
 */
public enum Acao {
    REGISTRAR_RECEBIMENTO,
    LANCAR_NO_CAIXA,
    CONFERIR_CAIXA,
    ABRIR_MES,
    REAJUSTAR_ALUGUEL,
    ESTORNAR,
    FECHAR_EXERCICIO,
    GERENCIAR_PESSOAS
}
