# Kosmos · guia de implementação em Java (POO)

Material para quem vai conduzir a implementação com apoio de IA. Tem três partes: o que é a plataforma, como os pilares de POO entram nela, e um prompt pronto para mandar à IA junto com os prints das telas.

---

## 1. O que é o Kosmos

Sistema desktop de controle de locações e caixa da Fundação Manuel Cruz, que administra 123 imóveis (122 alugados). Substitui as planilhas de Excel que o gestor usa hoje.

**Quem usa**
- **Davi Nogueira, gestor.** Acesso total.
- **Ana Beatriz Lins, funcionária.** Registra recebimentos e lançamentos, mas não estorna, não fecha o exercício e não gerencia pessoas.

**O que o sistema faz, por ordem de frequência**
1. Registrar o pagamento de um aluguel e imprimir o recibo (cerca de 122 vezes por mês).
2. Achar um imóvel ou inquilino pela busca (várias vezes por dia).
3. Lançar entradas e saídas no caixa (2 a 5 por dia).
4. Conferir o caixa e fechar o dia (1 vez por dia).
5. Abrir o mês seguinte e aplicar reajustes de aluguel (1 vez por mês).
6. Registrar solicitações de reparo nos imóveis e acompanhar o status.
7. Cadastrar funcionários e prestadores terceirizados (Supply).

**O que o sistema não faz:** redigir ou assinar contrato, emitir boleto, portal do inquilino, RH, consultar índice de reajuste em API.

## 2. Telas (correspondem aos prints)

| Tela | Para que serve |
|---|---|
| Abertura | Só a logo, antes do login |
| Login / Criar conta / Esqueci a senha | Entra com e-mail ou CPF e senha. Conta nova só com código de convite gerado pelo gestor |
| Início | O que falta hoje: números do mês, recibos a receber com botão Registrar, caixa do dia, "Precisa de atenção" |
| Recibos | Lista do mês com filtros (situação, forma, vencimento) e seletor de competência |
| Caixa | Movimento do dia: hora, histórico, origem, entrada, saída, saldo, quem lançou. Botões Novo lançamento, Estornar, Conferir e fechar o dia |
| Fechamento do dia (RDD) | Resumo do dia depois da conferência |
| Histórico | Tudo que foi feito, por quem e quando. Ninguém edita |
| Imóveis + Detalhe do imóvel | Lista e página do imóvel com recibos, solicitações e botões Registrar recebimento / Reajustar aluguel |
| Solicitações | Pedidos de reparo com status (na fila, em andamento, bloqueado, finalizado) |
| Supply | Funcionários e terceirizados, com status (ativo, inativo, bloqueado) |
| Prestação de contas | Os 12 meses do exercício, só para o gestor |
| Configurações | Meu perfil; para o gestor também Geral e Pessoas e funções (gerar código de convite, trocar função) |

**Modais (janelas que abrem por cima):** registrar recebimento (busca → formulário → revisão → recibo impresso), conferir caixa, novo lançamento, estornar, abrir o mês, reajustar aluguel, registrar imóvel, nova solicitação, cadastrar no Supply.

## 3. Regras de negócio que o código precisa garantir

São estas regras que dão sentido ao encapsulamento. Se alguma puder ser quebrada por fora da classe, o código está errado.

1. **Recibo não se edita depois de emitido.** Erro se corrige com estorno.
2. **Um recibo por competência.** Se o inquilino paga 3 meses atrasados, saem 3 recibos.
3. **Multa e juros são calculados, não digitados:** 2% de multa + 0,033% ao dia de atraso.
4. **Dinheiro vai para o caixa; PIX, transferência e depósito vão para o banco.**
5. **Dia fechado não aceita lançamento novo.**
6. **Divergência na conferência exige justificativa.**
7. **Aluguel só muda por reajuste**, e o reajuste fica registrado (valor antigo, novo, quem fez, a partir de quando).
8. **Todo lançamento guarda quem fez e quando.** Nada se apaga.
9. **Funcionário não estorna, não fecha exercício, não gerencia pessoas.**
10. **Conta nova só com código de convite válido e não usado.**

## 4. O que a disciplina pede em cada sprint

Do plano de ensino de Projetos 2:

| Sprint | Data de validação | Entrega de implementação |
|---|---|---|
| 7 | 19/10 | Criação de classes, métodos e atributos + protótipo de alta fidelidade |
| 8 | 26/10 | Interface com toolkit gráfico + **encapsulamento** |
| 9 | 09/11 | Interface com toolkit gráfico + **herança e polimorfismo** |
| 10 | 16/11 | Interface com toolkit gráfico + **métodos avançados** |
| SR2 | 23/11 | Apresentação final |

A sugestão é seguir essa ordem também ao pedir para a IA: não pedir o sistema inteiro de uma vez.

## 5. Modelo de classes

### Pacotes
```
kosmos/
  modelo/        classes do domínio (não sabem que existe tela)
  servico/       um serviço por fluxo
  repositorio/   guarda e busca os objetos (em memória, por enquanto)
  tela/          janelas Swing (uma por tela do protótipo)
  Main.java
```

### Classes do domínio (sprint 7)

| Classe | Atributos principais | Métodos principais |
|---|---|---|
| `Imovel` (abstrata) | endereco, situacao | `podeSerAlugado()` |
| `Apartamento`, `Casa`, `Loja`, `Sala`, `Terreno` | — | sobrescrevem o que muda |
| `Pessoa` (abstrata) | nome, cpf | — |
| `Inquilino` | telefone | — |
| `Usuario` (abstrata) | email, senha | `pode(Acao)`, `autenticar(senha)` |
| `Gestor`, `Funcionario` | — | sobrescrevem `pode(Acao)` |
| `Contrato` | imovel, inquilino, valorAluguel, diaVencimento, vigenteAte, reajustes | `reajustar(...)`, `estaVencido()` |
| `Reajuste` | valorAnterior, valorNovo, aPartirDe, feitoPor | — |
| `Competencia` | mes, ano, aberta | `abrir()`, `fechar()` |
| `Recibo` | numero, contrato, competencia, valor, encargos, forma, situacao, emitidoPor | `calcularEncargos(diasAtraso)`, `gerarDocumento()` |
| `Lancamento` (abstrata) | valor, tipo, hora, lancadoPor | `getOrigem()` |
| `LancamentoDeRecibo`, `LancamentoManual`, `Estorno` | origem / historico / original + justificativa | sobrescrevem `getOrigem()` |
| `Caixa` | lancamentos, saldoAnterior, fechado | `lancar(...)`, `saldo()`, `conferir(contado, justificativa)` |
| `Solicitacao` | imovel, servico, responsavel, status | `mudarStatus(...)` |
| `Prestador` | nome, documento, tipo, vinculo, servico, status | — |
| `Convite` | codigo, funcao, usadoPor | `usar(...)` |

Enums: `SituacaoRecibo` (NAO_EMITIDO, EMITIDO_NAO_PAGO, PAGO, EM_ATRASO), `StatusSolicitacao`, `StatusPrestador`, `TipoMovimento` (ENTRADA, SAIDA), `Acao` (ESTORNAR, FECHAR_EXERCICIO, GERENCIAR_PESSOAS...).

### Serviços (sprint 10, métodos avançados)
- `RecebimentoService.registrar(contrato, competencias, forma, usuario)`: gera um recibo por competência, calcula encargos, lança no destino certo, grava no histórico.
- `CompetenciaService.abrir(mes, reajustes)`: cria as previsões do mês e aplica os reajustes informados.
- `CaixaService.conferirEFechar(contado, justificativa, usuario)`
- `AutenticacaoService.entrar(login, senha)` e `cadastrar(codigo, dados)`
- `BuscaService.buscar(texto)`: procura em imóveis, inquilinos e recibos ao mesmo tempo.

## 6. Onde cada pilar aparece (para explicar na apresentação)

**Encapsulamento (sprint 8)**

Todo atributo é `private`. O acesso é sempre por getter e setter, e o setter **valida** antes de aceitar o valor. Onde a regra de negócio proíbe mudar, não existe setter, e isso também é encapsulamento.

| Classe | Getters | Setters (com validação) | Sem setter, por regra |
|---|---|---|---|
| `Imovel` | todos | `setEndereco` (não vazio), `setSituacao` | — |
| `Pessoa` / `Inquilino` | todos | `setNome` (não vazio), `setCpf` (11 dígitos), `setTelefone` | — |
| `Usuario` | `getEmail`, `getNome` | `setEmail` (tem @), `setSenha` (mínimo 6 caracteres; guarda só o hash) | sem `getSenha` |
| `Contrato` | todos | `setDiaVencimento` (1 a 28), `setVigenteAte` (data futura) | `valorAluguel`: muda só por `reajustar(...)` |
| `Competencia` | todos | — | `aberta`: muda só por `abrir()` e `fechar()` |
| `Recibo` | todos | — | nenhum: recibo emitido é imutável |
| `Lancamento` | todos | — | nenhum: corrige com `Estorno` |
| `Caixa` | `getSaldoAnterior`, `getLancamentos` (só leitura), `isFechado` | — | `saldo()` é calculado; `fechado` muda só por `conferir(...)` |
| `Solicitacao` | todos | `setServico`, `setResponsavel` (prestador ativo) | `status`: muda por `mudarStatus(...)`, que registra no histórico |
| `Prestador` | todos | `setNome`, `setDocumento` (CPF ou CNPJ), `setServico`, `setStatus` | — |
| `Convite` | todos | `setFuncao` (só antes de usado) | `usadoPor`: muda só por `usar(...)` |

Exemplo de setter com validação:

```java
public void setDiaVencimento(int dia) {
    if (dia < 1 || dia > 28) {
        throw new IllegalArgumentException("Dia de vencimento deve ser entre 1 e 28");
    }
    this.diaVencimento = dia;
}
```

E os casos em que a regra fecha o acesso:
- `Recibo` com atributos `private final` e sem setters: depois de criado, não muda.
- `Contrato.valorAluguel` é `private` e só muda por `reajustar(...)`, que valida o valor e registra o histórico.
- `Caixa.lancar(...)` recusa lançamento se o dia estiver fechado (lança `DiaFechadoException`).
- `Caixa.saldo()` é calculado a partir dos lançamentos, nunca guardado num atributo que alguém possa alterar.
- `getLancamentos()` devolve lista só de leitura (`Collections.unmodifiableList`).
- `Usuario.senha` nunca sai da classe: tem `setSenha(...)`, que valida e guarda o hash, mas não tem `getSenha()`. Para conferir, existe `autenticar(senha)`.

**Herança (sprint 9)**
- `Imovel` → `Apartamento`, `Casa`, `Loja`, `Sala`, `Terreno`.
- `Pessoa` → `Inquilino`, `Usuario` → `Gestor`, `Funcionario`.
- `Lancamento` → `LancamentoDeRecibo`, `LancamentoManual`, `Estorno`. A coluna "Origem" da tela de Caixa vem daqui.

**Polimorfismo (sprint 9)**
- `usuario.pode(Acao.ESTORNAR)`: o `Gestor` responde sim, o `Funcionario` responde não. A tela mostra ou esconde o botão sem perguntar qual é o perfil.
- Interface `FormaPagamento` com `destino()`: `Dinheiro` devolve o caixa; `Pix`, `Transferencia` e `Deposito` devolvem o banco. O registro de recebimento não tem nenhum `if` de forma de pagamento.
- `lancamento.getOrigem()`: cada tipo de lançamento escreve a sua origem.

**Abstração**
- Interfaces `Auditavel` (quem fez e quando) e `Imprimivel` (gera documento), implementadas por `Recibo` e `Lancamento`.

## 7. Prompt para mandar à IA

Mande junto com os prints das telas. Peça **uma sprint por vez**: copie o bloco de contexto e troque só a última parte.

```
Contexto do projeto
Estou desenvolvendo o Kosmos, um sistema desktop em Java para a Fundação Manuel Cruz
controlar o aluguel de 123 imóveis e o caixa da fundação. É um projeto da disciplina
de Programação Orientada a Objetos, então o código precisa mostrar claramente
classes, atributos, métodos, encapsulamento, herança e polimorfismo.

Anexei os prints das telas do protótipo. A interface deve seguir esses prints:
janela única de 1440×900, barra superior com logo, busca e botão "Registrar
recebimento", menu lateral (Início, Recibos, Caixa, Imóveis, Solicitações, Supply,
Prestação de contas). Ações que criam ou alteram dados abrem em janela modal.

Usuários: Gestor (acesso total) e Funcionário (não estorna, não fecha exercício,
não gerencia pessoas).

Regras de negócio obrigatórias:
1. Recibo não se edita depois de emitido; correção é por estorno.
2. Um recibo por mês pago.
3. Multa de 2% + juros de 0,033% ao dia, calculados pelo sistema.
4. Dinheiro entra no caixa; PIX, transferência e depósito entram no banco.
5. Dia fechado não aceita lançamento novo.
6. Divergência na conferência de caixa exige justificativa.
7. Aluguel só muda por reajuste, que fica registrado.
8. Todo lançamento guarda quem fez e quando; nada é apagado.
9. Conta nova só com código de convite gerado pelo gestor.

Tecnologia: Java 17, interface em Swing, dados em memória (sem banco por enquanto).
Pacotes: modelo, servico, repositorio, tela.

Como quero a resposta:
- Todo atributo private, com getters e setters. Os setters validam o valor
  e lançam IllegalArgumentException se for inválido. Onde uma regra de negócio
  proíbe a alteração, não crie setter e comente o porquê.
- Código comentado em português, explicando por que cada atributo é private,
  onde há herança e onde há polimorfismo.
- Depois do código, um parágrafo curto dizendo qual pilar de POO cada classe demonstra.
- Não invente telas ou funções além das dos prints.

[ESCOLHA UMA DAS ETAPAS ABAIXO]
```

**Etapa sprint 7:**
```
Crie as classes do pacote modelo com atributos, construtores, getters, setters
e métodos:
Imovel (abstrata) e subclasses Apartamento, Casa, Loja, Sala, Terreno; Pessoa
(abstrata), Inquilino, Usuario (abstrata), Gestor, Funcionario; Contrato, Reajuste,
Competencia, Recibo, Lancamento (abstrata), Caixa, Solicitacao, Prestador, Convite;
e os enums SituacaoRecibo, StatusSolicitacao, StatusPrestador, TipoMovimento, Acao.
Ainda sem tela.
```

**Etapa sprint 8:**
```
Revise as classes para aplicar encapsulamento de verdade: todos os atributos
private, getters e setters em todas as classes, com validação dentro de cada
setter. Retire os setters que permitiriam quebrar as regras 1, 5, 6, 7 e 8
(Recibo, Lancamento, Contrato.valorAluguel, Caixa.fechado) e troque por métodos
com nome de ação (reajustar, conferir, estornar). Usuario tem setSenha mas não
tem getSenha. Crie as exceções DiaFechadoException e ReciboImutavelException.
Crie a janela principal em Swing (barra superior + menu lateral + área de conteúdo)
e a tela Início, seguindo o print.
```

**Etapa sprint 9:**
```
Aplique herança e polimorfismo: Lancamento com subclasses LancamentoDeRecibo,
LancamentoManual e Estorno sobrescrevendo getOrigem(); Usuario.pode(Acao)
sobrescrito em Gestor e Funcionario; interface FormaPagamento com destino()
implementada por Dinheiro, Pix, Transferencia e Deposito.
Crie as telas Recibos, Caixa e Imóveis em Swing, seguindo os prints, usando
usuario.pode(...) para mostrar ou esconder botões.
```

**Etapa sprint 10:**
```
Crie os serviços RecebimentoService, CompetenciaService, CaixaService,
AutenticacaoService e BuscaService com a lógica completa (um recibo por
competência, cálculo de encargos, conferência com divergência, login por e-mail
ou CPF, cadastro por convite). Ligue os serviços aos modais: registrar recebimento
(busca, formulário, revisão, recibo), conferir caixa, novo lançamento, estorno,
reajuste e login.
```

## 8. Cuidado com a regra de IA da disciplina

O plano de ensino diz que, em atividade prática de programação, o uso de IA segue as regras de cada enunciado, e que **o aluno precisa compreender, explicar e justificar a solução**. Em prova, IA não é permitida.

Na prática:
- Confiram no enunciado de cada sprint se a IA é permitida e se é preciso declarar o uso.
- Leiam o código antes de entregar. O professor pode perguntar por que um atributo é `private` ou onde está o polimorfismo, e quem vai responder são vocês.
- O prompt já pede comentários explicativos para ajudar nisso. Usem esses comentários para estudar, não para colar na apresentação.