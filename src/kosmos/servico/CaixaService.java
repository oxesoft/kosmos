package kosmos.servico;

import kosmos.modelo.Acao;
import kosmos.modelo.Caixa;
import kosmos.modelo.Estorno;
import kosmos.modelo.Lancamento;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;

/**
 * Serviço responsável pela conferência, fechamento e estornos no Caixa
 * (Seção 5 do README.md: CaixaService.conferirEFechar(contado, justificativa, usuario)).
 */
public class CaixaService {

    private final KosmosRepositorio repositorio;

    public CaixaService(KosmosRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repositorio = repositorio;
    }

    public void conferirEFechar(double contado, String justificativa, Usuario usuario) {
        if (usuario == null || !usuario.pode(Acao.CONFERIR_CAIXA)) {
            throw new IllegalArgumentException("Usuário inválido ou sem permissão para conferir o caixa.");
        }
        Caixa caixa = repositorio.getCaixaAtual();
        caixa.conferir(contado, justificativa);
    }

    public Estorno estornar(Lancamento original, String justificativa, Usuario usuario) {
        Estorno estorno = new Estorno(original, justificativa, usuario);
        repositorio.getCaixaAtual().lancar(estorno);
        repositorio.registrarAuditoria(estorno);
        return estorno;
    }
}
