package kosmos;

import kosmos.modelo.Usuario;
import kosmos.repositorio.DadosIniciais;
import kosmos.repositorio.KosmosRepositorio;
import kosmos.servico.AutenticacaoService;
import kosmos.tela.MenuConsole;

/**
 * Ponto de entrada do sistema Kosmos.
 * Inicializa a base de dados em memória e executa a interface interativa de console com formulários.
 */
public class Main {

    public static void main(String[] args) {
        KosmosRepositorio repositorio = new KosmosRepositorio();
        AutenticacaoService authService = new AutenticacaoService(repositorio);

        // Carrega dados iniciais da fundação (imóveis, contratos, inquilinos, supply e caixa)
        Usuario usuarioInicial = DadosIniciais.carregarDados(repositorio, authService);

        // Inicia a aplicação com menu interativo e formulários
        MenuConsole menu = new MenuConsole(repositorio, usuarioInicial);
        menu.iniciar();
    }
}
