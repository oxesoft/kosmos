package kosmos.modelo;

import java.time.LocalDateTime;

/**
 * Interface que representa a capacidade de auditoria (quem fez e quando),
 * implementada por Recibo, Lancamento e Reajuste (Seção 6 - Abstração e Regra 8).
 */
public interface Auditavel {

    Usuario getResponsavelAcao();

    LocalDateTime getDataHoraAcao();
}
