package kosmos.modelo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Classe abstrata que representa um Usuário autenticável do sistema Kosmos.
 *
 * Demonstra:
 * - Herança: herda de Pessoa e é superclasse de Gestor e Funcionario.
 * - Encapsulamento estrito: a senha nunca sai da classe (não existe getSenha()),
 *   e setSenha() valida no mínimo 6 caracteres e armazena apenas o hash SHA-256.
 * - Polimorfismo: método abstrato pode(Acao), sobrescrito por Gestor e Funcionario.
 */
public abstract class Usuario extends Pessoa {

    private String email;
    private String senhaHash; // Regra da Seção 6: guarda só o hash; sem getSenha()

    public Usuario(String nome, String cpf, String email, String senhaTextoPuro) {
        super(nome, cpf);
        setEmail(email);
        setSenha(senhaTextoPuro);
    }

    /**
     * Método polimórfico que define se este perfil de usuário tem permissão para realizar a ação.
     */
    public abstract boolean pode(Acao acao);

    /**
     * Confere se a senha informada corresponde ao hash armazenado, sem expor a senha.
     */
    public boolean autenticar(String senhaInformada) {
        if (senhaInformada == null) {
            return false;
        }
        return this.senhaHash.equals(gerarHash(senhaInformada));
    }

    public String getEmail() {
        return email;
    }

    /**
     * Valida que o e-mail contenha '@' (Tabela de Encapsulamento - Seção 6).
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@") || email.trim().length() < 3) {
            throw new IllegalArgumentException("E-mail inválido: deve conter '@'.");
        }
        this.email = email.trim();
    }

    /**
     * Valida que a senha tenha no mínimo 6 caracteres e guarda apenas o seu hash.
     * NÃO existe getSenha() por regra de encapsulamento e segurança (Seção 6).
     */
    public void setSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.");
        }
        this.senhaHash = gerarHash(senha);
    }

    private String gerarHash(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo de hash SHA-256 indisponível.", e);
        }
    }
}
