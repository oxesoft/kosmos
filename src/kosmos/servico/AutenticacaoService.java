package kosmos.servico;

import kosmos.modelo.Convite;
import kosmos.modelo.Funcionario;
import kosmos.modelo.Gestor;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;

/**
 * Serviço responsável por autenticação (login por e-mail ou CPF) e criação de conta
 * mediante código de convite (Seção 5 e Regra 10 do README.md).
 */
public class AutenticacaoService {

    private final KosmosRepositorio repositorio;

    public AutenticacaoService(KosmosRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repositorio = repositorio;
    }

    /**
     * Autentica um usuário por e-mail ou CPF e senha.
     */
    public Usuario entrar(String login, String senha) {
        if (login == null || senha == null) {
            throw new IllegalArgumentException("Login e senha devem ser informados.");
        }
        String loginLimpo = login.trim();
        for (Usuario u : repositorio.getUsuarios()) {
            boolean mesmoEmail = u.getEmail().equalsIgnoreCase(loginLimpo);
            boolean mesmoCpf = u.getCpf().equals(loginLimpo)
                    || u.getCpf().replaceAll("\\D", "").equals(loginLimpo.replaceAll("\\D", ""));
            if ((mesmoEmail || mesmoCpf) && u.autenticar(senha)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Credenciais inválidas.");
    }

    /**
     * Regra 10: "Conta nova só com código de convite válido e não usado."
     */
    public Usuario cadastrar(String codigoConvite, String nome, String cpf, String email, String senha) {
        if (codigoConvite == null || codigoConvite.trim().isEmpty()) {
            throw new IllegalArgumentException("O código de convite é obrigatório para criar conta.");
        }
        Convite conviteEncontrado = null;
        for (Convite c : repositorio.getConvites()) {
            if (c.getCodigo().equals(codigoConvite.trim())) {
                conviteEncontrado = c;
                break;
            }
        }
        if (conviteEncontrado == null || conviteEncontrado.isUsado()) {
            throw new IllegalArgumentException("Código de convite inválido ou já utilizado.");
        }

        Usuario novoUsuario;
        if ("GESTOR".equalsIgnoreCase(conviteEncontrado.getFuncao())) {
            novoUsuario = new Gestor(nome, cpf, email, senha);
        } else {
            novoUsuario = new Funcionario(nome, cpf, email, senha);
        }

        conviteEncontrado.usar(novoUsuario);
        repositorio.adicionarUsuario(novoUsuario);
        return novoUsuario;
    }
}
