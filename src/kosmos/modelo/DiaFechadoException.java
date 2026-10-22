package kosmos.modelo;

/**
 * Exceção de domínio lançada quando se tenta registrar um novo lançamento
 * em um Caixa cujo dia já foi conferido e fechado (Regra de negócio 5).
 */
public class DiaFechadoException extends RuntimeException {

    public DiaFechadoException(String mensagem) {
        super(mensagem);
    }
}
