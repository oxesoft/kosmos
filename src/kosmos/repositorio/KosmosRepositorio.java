package kosmos.repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kosmos.modelo.Auditavel;
import kosmos.modelo.Caixa;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.Convite;
import kosmos.modelo.Imovel;
import kosmos.modelo.Inquilino;
import kosmos.modelo.Prestador;
import kosmos.modelo.Recibo;
import kosmos.modelo.Solicitacao;
import kosmos.modelo.Usuario;

/**
 * Repositório em memória que armazena e consulta os objetos do domínio Kosmos
 * (Seção 5 do README.md).
 */
public class KosmosRepositorio {

    private final List<Imovel> imoveis = new ArrayList<>();
    private final List<Inquilino> inquilinos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Contrato> contratos = new ArrayList<>();
    private final List<Competencia> competencias = new ArrayList<>();
    private final List<Recibo> recibos = new ArrayList<>();
    private final List<Prestador> prestadores = new ArrayList<>();
    private final List<Solicitacao> solicitacoes = new ArrayList<>();
    private final List<Convite> convites = new ArrayList<>();
    private final List<Auditavel> historicoAuditoria = new ArrayList<>();
    private Caixa caixaAtual = new Caixa(0.0);
    private double saldoBanco = 0.0;
    private int sequencialRecibo = 1;

    public String proximoNumeroRecibo(int ano) {
        return String.format("%d/%04d", ano, sequencialRecibo++);
    }

    public void adicionarImovel(Imovel imovel) {
        imoveis.add(imovel);
    }

    public void adicionarInquilino(Inquilino inquilino) {
        inquilinos.add(inquilino);
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void adicionarContrato(Contrato contrato) {
        contratos.add(contrato);
    }

    public void adicionarCompetencia(Competencia competencia) {
        competencias.add(competencia);
    }

    public void adicionarRecibo(Recibo recibo) {
        recibos.add(recibo);
        historicoAuditoria.add(recibo);
    }

    public void adicionarPrestador(Prestador prestador) {
        prestadores.add(prestador);
    }

    public void adicionarSolicitacao(Solicitacao solicitacao) {
        solicitacoes.add(solicitacao);
    }

    public void adicionarConvite(Convite convite) {
        convites.add(convite);
    }

    public void registrarAuditoria(Auditavel evento) {
        historicoAuditoria.add(evento);
    }

    public void creditarNoBanco(double valor) {
        if (valor > 0) {
            this.saldoBanco += valor;
        }
    }

    public Caixa getCaixaAtual() {
        return caixaAtual;
    }

    public void setCaixaAtual(Caixa caixaAtual) {
        if (caixaAtual == null) {
            throw new IllegalArgumentException("O caixa atual não pode ser nulo.");
        }
        this.caixaAtual = caixaAtual;
    }

    public double getSaldoBanco() {
        return saldoBanco;
    }

    public List<Imovel> getImoveis() {
        return Collections.unmodifiableList(imoveis);
    }

    public List<Inquilino> getInquilinos() {
        return Collections.unmodifiableList(inquilinos);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Contrato> getContratos() {
        return Collections.unmodifiableList(contratos);
    }

    public List<Competencia> getCompetencias() {
        return Collections.unmodifiableList(competencias);
    }

    public List<Recibo> getRecibos() {
        return Collections.unmodifiableList(recibos);
    }

    public List<Prestador> getPrestadores() {
        return Collections.unmodifiableList(prestadores);
    }

    public List<Solicitacao> getSolicitacoes() {
        return Collections.unmodifiableList(solicitacoes);
    }

    public List<Convite> getConvites() {
        return Collections.unmodifiableList(convites);
    }

    public List<Auditavel> getHistoricoAuditoria() {
        return Collections.unmodifiableList(historicoAuditoria);
    }
}
