package kosmos.modelo;

/**
 * Interface para entidades que geram documento formatado para impressão
 * (implementada por Recibo e Lancamento, conforme Seção 6 - Abstração do README.md).
 */
public interface Imprimivel {

    String gerarDocumento();
}
