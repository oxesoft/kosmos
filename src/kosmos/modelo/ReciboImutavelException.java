package kosmos.modelo;

/**
 * Exceção de domínio lançada quando se tenta alterar um Recibo já emitido
 * (Regra de negócio 1: Recibo não se edita depois de emitido; erro se corrige com estorno).
 */
public class ReciboImutavelException extends RuntimeException {

    public ReciboImutavelException(String mensagem) {
        super(mensagem);
    }
}
