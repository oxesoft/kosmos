package kosmos.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o Contrato de locação entre um Imovel e um Inquilino (Seções 5 e 6 do README.md).
 *
 * Demonstra:
 * - Encapsulamento: todos os atributos são private; setDiaVencimento valida entre 1 e 28;
 *   setVigenteAte valida data futura; getReajustes devolve lista imutável.
 * - Regra de negócio 7 sem setter: NÃO existe setValorAluguel(). O valor do aluguel só muda
 *   pelo método de ação reajustar(...), que valida o novo valor e registra um Reajuste no histórico.
 */
public class Contrato {

    private Imovel imovel;
    private Inquilino inquilino;
    private double valorAluguel; // Sem setter! Muda apenas por reajustar(...)
    private int diaVencimento;
    private LocalDate vigenteAte;
    private final List<Reajuste> reajustes;

    public Contrato(Imovel imovel, Inquilino inquilino, double valorAluguelInicial,
                    int diaVencimento, LocalDate vigenteAte) {
        setImovel(imovel);
        setInquilino(inquilino);
        if (valorAluguelInicial <= 0) {
            throw new IllegalArgumentException("O valor inicial do aluguel deve ser maior que zero.");
        }
        this.valorAluguel = valorAluguelInicial;
        setDiaVencimento(diaVencimento);
        setVigenteAte(vigenteAte);
        this.reajustes = new ArrayList<>();
        this.imovel.setSituacao("alugado");
    }

    /**
     * Regra 7: "Aluguel só muda por reajuste, e o reajuste fica registrado
     * (valor antigo, novo, quem fez, a partir de quando)."
     */
    public Reajuste reajustar(double novoValor, LocalDate aPartirDe, Usuario feitoPor) {
        if (novoValor <= 0) {
            throw new IllegalArgumentException("O novo valor do aluguel deve ser maior que zero.");
        }
        if (novoValor == this.valorAluguel) {
            throw new IllegalArgumentException("O novo valor do aluguel deve ser diferente do valor atual.");
        }
        if (feitoPor == null || !feitoPor.pode(Acao.REAJUSTAR_ALUGUEL)) {
            throw new IllegalArgumentException("Usuário inválido ou sem permissão para reajustar aluguel.");
        }
        Reajuste registro = new Reajuste(this.valorAluguel, novoValor, aPartirDe, feitoPor);
        this.valorAluguel = novoValor;
        this.reajustes.add(registro);
        return registro;
    }

    /**
     * Verifica se o contrato está vencido em relação à data de hoje.
     */
    public boolean estaVencido() {
        return estaVencido(LocalDate.now());
    }

    /**
     * Verifica se o contrato está vencido em relação a uma data de referência.
     */
    public boolean estaVencido(LocalDate dataReferencia) {
        if (dataReferencia == null) {
            throw new IllegalArgumentException("A data de referência não pode ser nula.");
        }
        return vigenteAte.isBefore(dataReferencia);
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        if (imovel == null) {
            throw new IllegalArgumentException("O imóvel do contrato não pode ser nulo.");
        }
        this.imovel = imovel;
    }

    public Inquilino getInquilino() {
        return inquilino;
    }

    public void setInquilino(Inquilino inquilino) {
        if (inquilino == null) {
            throw new IllegalArgumentException("O inquilino do contrato não pode ser nulo.");
        }
        this.inquilino = inquilino;
    }

    public double getValorAluguel() {
        return valorAluguel;
    }
    // NÃO existe setValorAluguel(): violaria a Regra 7 do README.md.

    public int getDiaVencimento() {
        return diaVencimento;
    }

    /**
     * Valida que o dia de vencimento esteja entre 1 e 28 (Seção 6 do README.md).
     */
    public void setDiaVencimento(int dia) {
        if (dia < 1 || dia > 28) {
            throw new IllegalArgumentException("Dia de vencimento deve ser entre 1 e 28");
        }
        this.diaVencimento = dia;
    }

    public LocalDate getVigenteAte() {
        return vigenteAte;
    }

    /**
     * Valida que a data de vigência do contrato seja futura (Seção 6 do README.md).
     */
    public void setVigenteAte(LocalDate vigenteAte) {
        if (vigenteAte == null || !vigenteAte.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data 'vigente até' do contrato deve ser uma data futura.");
        }
        this.vigenteAte = vigenteAte;
    }

    /**
     * Retorna lista imutável do histórico de reajustes para proteger o encapsulamento.
     */
    public List<Reajuste> getReajustes() {
        return Collections.unmodifiableList(reajustes);
    }
}
