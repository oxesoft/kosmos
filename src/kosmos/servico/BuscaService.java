package kosmos.servico;

import java.util.ArrayList;
import java.util.List;
import kosmos.modelo.Imovel;
import kosmos.modelo.Inquilino;
import kosmos.modelo.Recibo;
import kosmos.repositorio.KosmosRepositorio;

/**
 * Serviço de busca unificada que procura simultaneamente em imóveis, inquilinos e recibos
 * (Seção 5 do README.md: BuscaService.buscar(texto)).
 */
public class BuscaService {

    private final KosmosRepositorio repositorio;

    public BuscaService(KosmosRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repositorio = repositorio;
    }

    public List<Object> buscar(String texto) {
        List<Object> resultados = new ArrayList<>();
        if (texto == null || texto.trim().isEmpty()) {
            return resultados;
        }
        String termo = texto.trim().toLowerCase();

        for (Imovel imovel : repositorio.getImoveis()) {
            if (imovel.getEndereco().toLowerCase().contains(termo)
                    || imovel.getTipo().toLowerCase().contains(termo)) {
                resultados.add(imovel);
            }
        }

        for (Inquilino inquilino : repositorio.getInquilinos()) {
            if (inquilino.getNome().toLowerCase().contains(termo)
                    || inquilino.getCpf().contains(termo)) {
                resultados.add(inquilino);
            }
        }

        for (Recibo recibo : repositorio.getRecibos()) {
            if (recibo.getNumero().toLowerCase().contains(termo)
                    || recibo.getContrato().getInquilino().getNome().toLowerCase().contains(termo)
                    || recibo.getContrato().getImovel().getEndereco().toLowerCase().contains(termo)) {
                resultados.add(recibo);
            }
        }

        return resultados;
    }
}
