package kosmos.modelo;

/**
 * Representa um Prestador cadastrado no módulo Supply (funcionário ou terceirizado)
 * conforme Seções 5 e 6 do README.md.
 *
 * Demonstra:
 * - Encapsulamento: todos os atributos são private com getters e setters validados,
 *   incluindo validação de CPF (11 dígitos) ou CNPJ (14 dígitos) em setDocumento().
 */
public class Prestador {

    private String nome;
    private String documento; // CPF (11 dígitos) ou CNPJ (14 dígitos)
    private String tipo;      // "Pessoa" ou "Empresa"
    private String vinculo;   // "Direto" ou "Terceirizado"
    private String servico;
    private StatusPrestador status;

    public Prestador(String nome, String documento, String tipo,
                     String vinculo, String servico, StatusPrestador status) {
        setNome(nome);
        setDocumento(documento);
        setTipo(tipo);
        setVinculo(vinculo);
        setServico(servico);
        setStatus(status);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do prestador não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getDocumento() {
        return documento;
    }

    /**
     * Valida se o documento informado é um CPF (11 dígitos) ou CNPJ (14 dígitos) válido em tamanho.
     */
    public void setDocumento(String documento) {
        if (documento == null) {
            throw new IllegalArgumentException("O documento (CPF ou CNPJ) não pode ser nulo.");
        }
        String digitos = documento.replaceAll("\\D", "");
        if (digitos.length() != 11 && digitos.length() != 14) {
            throw new IllegalArgumentException("O documento do prestador deve ser um CPF (11 dígitos) ou CNPJ (14 dígitos).");
        }
        this.documento = documento.trim();
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("O tipo do prestador ('Pessoa' ou 'Empresa') não pode ser vazio.");
        }
        this.tipo = tipo.trim();
    }

    public String getVinculo() {
        return vinculo;
    }

    public void setVinculo(String vinculo) {
        if (vinculo == null || vinculo.trim().isEmpty()) {
            throw new IllegalArgumentException("O vínculo do prestador ('Direto' ou 'Terceirizado') não pode ser vazio.");
        }
        this.vinculo = vinculo.trim();
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        if (servico == null || servico.trim().isEmpty()) {
            throw new IllegalArgumentException("O serviço prestado não pode ser vazio.");
        }
        this.servico = servico.trim();
    }

    public StatusPrestador getStatus() {
        return status;
    }

    public void setStatus(StatusPrestador status) {
        if (status == null) {
            throw new IllegalArgumentException("O status do prestador não pode ser nulo.");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s | %s) [%s]",
                nome, tipo, vinculo, servico, status.getDescricao());
    }
}
