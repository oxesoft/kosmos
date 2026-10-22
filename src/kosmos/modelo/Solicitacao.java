package kosmos.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa uma Solicitação de reparo ou serviço em um Imóvel (Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: setResponsavel valida que o prestador esteja ATIVO no Supply;
 *   NÃO existe setStatus(), pois o status só muda pelo método mudarStatus(...),
 *   que registra a transição no histórico.
 */
public class Solicitacao {

    private Imovel imovel;
    private String servico;
    private Prestador responsavel;
    private StatusSolicitacao status; // Sem setter direto: muda apenas por mudarStatus(...)
    private final List<String> historicoMudancas;

    public Solicitacao(Imovel imovel, String servico, Prestador responsavel) {
        setImovel(imovel);
        setServico(servico);
        setResponsavel(responsavel);
        this.status = StatusSolicitacao.NA_FILA;
        this.historicoMudancas = new ArrayList<>();
        this.historicoMudancas.add(LocalDateTime.now() + " - Criada com status: " + status.getDescricao());
    }

    /**
     * Altera o status da solicitação e registra a alteração no histórico (Seção 6 do README.md).
     */
    public void mudarStatus(StatusSolicitacao novoStatus, Usuario feitoPor) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status da solicitação não pode ser nulo.");
        }
        if (feitoPor == null) {
            throw new IllegalArgumentException("O usuário que alterou o status deve ser informado.");
        }
        StatusSolicitacao anterior = this.status;
        this.status = novoStatus;
        this.historicoMudancas.add(String.format("%s - Status alterado de '%s' para '%s' por %s",
                LocalDateTime.now(), anterior.getDescricao(), novoStatus.getDescricao(), feitoPor.getNome()));
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        if (imovel == null) {
            throw new IllegalArgumentException("O imóvel da solicitação não pode ser nulo.");
        }
        this.imovel = imovel;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        if (servico == null || servico.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do serviço solicitado não pode ser vazia.");
        }
        this.servico = servico.trim();
    }

    public Prestador getResponsavel() {
        return responsavel;
    }

    /**
     * Valida que o prestador responsável esteja com status ATIVO (Seção 6 do README.md).
     */
    public void setResponsavel(Prestador responsavel) {
        if (responsavel == null) {
            throw new IllegalArgumentException("O prestador responsável não pode ser nulo.");
        }
        if (responsavel.getStatus() != StatusPrestador.ATIVO) {
            throw new IllegalArgumentException(
                    "O prestador '" + responsavel.getNome() + "' não está ativo no Supply.");
        }
        this.responsavel = responsavel;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }
    // NÃO existe setStatus(): deve-se usar mudarStatus(...) para manter a trilha de auditoria.

    public List<String> getHistoricoMudancas() {
        return Collections.unmodifiableList(historicoMudancas);
    }

    @Override
    public String toString() {
        return String.format("Solicitação [%s | Serviço: %s | Resp.: %s | Status: %s]",
                imovel.getEndereco(), servico, responsavel.getNome(), status.getDescricao());
    }
}
