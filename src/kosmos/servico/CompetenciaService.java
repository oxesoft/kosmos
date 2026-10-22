package kosmos.servico;

import java.time.LocalDate;
import java.util.Map;
import kosmos.modelo.Competencia;
import kosmos.modelo.Contrato;
import kosmos.modelo.Reajuste;
import kosmos.modelo.Usuario;
import kosmos.repositorio.KosmosRepositorio;

/**
 * Serviço responsável por abrir o mês seguinte e aplicar reajustes de aluguel
 * (Seção 5 do README.md: CompetenciaService.abrir(mes, reajustes)).
 */
public class CompetenciaService {

    private final KosmosRepositorio repositorio;

    public CompetenciaService(KosmosRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("O repositório não pode ser nulo.");
        }
        this.repositorio = repositorio;
    }

    /**
     * Abre uma nova competência e aplica os reajustes informados para os contratos selecionados.
     */
    public Competencia abrir(Competencia novaCompetencia, Map<Contrato, Double> novosValoresAluguel,
                             Usuario usuario) {
        if (novaCompetencia == null) {
            throw new IllegalArgumentException("A competência a abrir não pode ser nula.");
        }
        novaCompetencia.abrir();
        repositorio.adicionarCompetencia(novaCompetencia);

        if (novosValoresAluguel != null) {
            LocalDate inicioVigencia = LocalDate.of(novaCompetencia.getAno(), novaCompetencia.getMes(), 1);
            for (Map.Entry<Contrato, Double> entry : novosValoresAluguel.entrySet()) {
                Contrato contrato = entry.getKey();
                Double novoValor = entry.getValue();
                if (contrato != null && novoValor != null) {
                    Reajuste reajuste = contrato.reajustar(novoValor, inicioVigencia, usuario);
                    repositorio.registrarAuditoria(reajuste);
                }
            }
        }
        return novaCompetencia;
    }
}
