package kosmos.modelo;

/**
 * Subclasse de Lancamento que representa o Estorno de um lançamento anterior
 * (Regras 1, 8 e 9 e Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Herança: especializa a classe abstrata Lancamento.
 * - Polimorfismo: sobrescreve getOrigem() e usa usuario.pode(Acao.ESTORNAR) para garantir
 *   que apenas o Gestor possa estornar (Regra 9).
 */
public class Estorno extends Lancamento {

    private final Lancamento original;
    private final String justificativa;

    public Estorno(Lancamento original, String justificativa, Usuario feitoPor) {
        super(
                validarOriginal(original).getValor(),
                inverterTipo(original.getTipo()),
                validarPermissaoEstorno(feitoPor)
        );
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("A justificativa do estorno é obrigatória.");
        }
        this.original = original;
        this.justificativa = justificativa.trim();
    }

    private static Lancamento validarOriginal(Lancamento original) {
        if (original == null) {
            throw new IllegalArgumentException("O lançamento original a ser estornado não pode ser nulo.");
        }
        return original;
    }

    private static TipoMovimento inverterTipo(TipoMovimento tipoOriginal) {
        return (tipoOriginal == TipoMovimento.ENTRADA) ? TipoMovimento.SAIDA : TipoMovimento.ENTRADA;
    }

    private static Usuario validarPermissaoEstorno(Usuario usuario) {
        if (usuario == null || !usuario.pode(Acao.ESTORNAR)) {
            throw new IllegalArgumentException("Permissão negada: apenas o Gestor pode realizar estornos.");
        }
        return usuario;
    }

    @Override
    public String getOrigem() {
        return "estorno (" + original.getOrigem() + ")";
    }

    @Override
    public String getHistorico() {
        return "Estorno: " + original.getHistorico() + " - Motivo: " + justificativa;
    }

    public Lancamento getOriginal() {
        return original;
    }

    public String getJustificativa() {
        return justificativa;
    }
}
