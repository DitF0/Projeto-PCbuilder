# PC Builder ESEG

**Programação Web — 6º Semestre/2026**
**Engenharia da Computação**
**Prof. Israel Florentino**

---

## Sumário

1. [Tema](#1-tema)
2. [Problema](#2-problema)
3. [Entidades](#3-entidades)
4. [Regras de Negócio](#4-regras-de-negócio)
5. [Dashboard](#5-dashboard)
6. [Tecnologias Utilizadas](#6-tecnologias-utilizadas)
7. [Arquitetura do Sistema](#7-arquitetura-do-sistema)
8. [Como Executar](#8-como-executar)
9. [Acesso ao Sistema](#9-acesso-ao-sistema)

---

## 1. Tema

**Sistema de Montagem e Orçamento de Workstations — PC Builder**

O PC Builder é uma aplicação web para montagem e gerenciamento de configurações de computadores. O sistema permite que usuários montem, salvem, editem e excluam builds (configurações de PC), selecionando componentes reais de hardware organizados por categoria e fabricante.

---

## 2. Problema

Montar um computador exige conhecimento técnico sobre compatibilidade de componentes, consumo energético e orçamento. Usuários iniciantes frequentemente montam configurações inviáveis — escolhendo fontes de alimentação insuficientes para os componentes selecionados, ou orçamentos inconsistentes.

O PC Builder resolve esse problema oferecendo:

- Um catálogo organizado de componentes reais divididos em 5 faixas de desempenho (Muito Fraco, Fraco, Médio, Ótimo, Excelente)
- Validação automática de compatibilidade energética antes de salvar qualquer configuração
- Controle de acesso para que cada usuário veja apenas suas próprias builds
- Um painel de controle com indicadores de desempenho e resumo financeiro

---

## 3. Entidades

O sistema possui 6 entidades mapeadas com JPA/Hibernate.

### 3.1 Usuario

Representa um usuário do sistema. Implementa `UserDetails` do Spring Security para autenticação.

| Campo  | Tipo    | Descrição                          |
|--------|---------|------------------------------------|
| id     | Long    | Chave primária (auto-incremento)   |
| nome   | String  | Nome de exibição do usuário        |
| email  | String  | Login do usuário (único no banco)  |
| senha  | String  | Hash BCrypt da senha               |
| role   | String  | Papel: ROLE_ADMIN ou ROLE_USER     |

### 3.2 Fabricante

Representa o fabricante de um componente (Intel, AMD, NVIDIA, Corsair, etc.).

| Campo | Tipo   | Descrição                       |
|-------|--------|---------------------------------|
| id    | Long   | Chave primária                  |
| nome  | String | Nome do fabricante              |

### 3.3 Categoria

Representa a categoria de um componente (Processador, Placa de Vídeo, Memória RAM, etc.).

| Campo | Tipo   | Descrição                       |
|-------|--------|---------------------------------|
| id    | Long   | Chave primária                  |
| nome  | String | Nome da categoria               |

O sistema conta com 7 categorias: Processador, Placa de Vídeo, Memória RAM, Armazenamento, Placa-Mãe, Cooler e Fonte.

### 3.4 Componente

Entidade central do sistema. Representa uma peça de hardware com seus atributos técnicos e financeiros.

| Campo        | Tipo    | Descrição                                          |
|--------------|---------|----------------------------------------------------|
| id           | Long    | Chave primária                                     |
| nome         | String  | Nome do componente                                 |
| especificacao| String  | Detalhes técnicos (frequência, memória, etc.)      |
| preco        | Double  | Preço de venda em reais                            |
| tdpWatts     | Integer | Consumo em Watts (para Fontes: capacidade em Watts)|
| categoria    | Categoria | Categoria do componente (FK)                    |
| fabricante   | Fabricante | Fabricante do componente (FK)                  |

Os componentes estão divididos em 5 faixas de desempenho por categoria:

| Faixa        | Exemplo de Processador | Perfil de Uso                          |
|--------------|------------------------|----------------------------------------|
| Muito Fraco  | Intel Celeron G6900    | Escritório, navegação básica           |
| Fraco        | Intel Core i3-12100    | Uso doméstico, jogos leves             |
| Médio        | AMD Ryzen 5 5600       | Trabalho e jogos do cotidiano          |
| Ótimo        | Intel Core i7-13700K   | Workstation, jogos de alto desempenho  |
| Excelente    | AMD Ryzen 9 7950X      | Produção profissional, edição 4K       |

### 3.5 Configuracao

Representa uma build — a configuração de PC montada por um usuário. Vincula um usuário a um conjunto de componentes.

| Campo     | Tipo                   | Descrição                                |
|-----------|------------------------|------------------------------------------|
| id        | Long                   | Chave primária                           |
| nomeBuild | String                 | Nome dado pelo usuário à configuração    |
| usuario   | Usuario                | Dono da build (FK)                       |
| itens     | List\<ItemConfiguracao\> | Componentes desta configuração         |

Métodos calculados (não persistidos no banco):
- `getValorTotal()` — soma o preço de todos os itens
- `getTdpTotal()` — soma o consumo de todos os itens
- `getValorTotalFormatado()` — retorna o valor no formato `R$ 1.234,56`

### 3.6 ItemConfiguracao — Classe de Relacionamento

Resolve o relacionamento N:M entre `Configuracao` e `Componente`, adicionando o atributo `quantidade` que um `@ManyToMany` simples não suportaria.

| Campo        | Tipo        | Descrição                         |
|--------------|-------------|-----------------------------------|
| id           | Long        | Chave primária                    |
| configuracao | Configuracao| Build à qual o item pertence (FK) |
| componente   | Componente  | Componente selecionado (FK)       |
| quantidade   | Integer     | Quantidade deste componente       |

Métodos calculados:
- `getPrecoTotal()` — preço unitário × quantidade
- `getTdpTotal()` — TDP unitário × quantidade

---

## 4. Regras de Negócio

O sistema implementa 3 regras de negócio que vão além do CRUD, todas aplicadas no `ConfiguracaoService` antes de qualquer persistência.

### Regra 1 — Mínimo de 3 Categorias Distintas

Uma build só pode ser salva se contiver componentes de pelo menos 3 categorias diferentes.

**Justificativa:** Impede configurações triviais ou incompletas (ex: apenas adicionar memórias sem processador ou armazenamento).

**Implementação:**
```java
long categoriasDiferentes = build.getItens().stream()
        .map(item -> item.getComponente().getCategoria().getId())
        .distinct()
        .count();

if (categoriasDiferentes < 3) {
    throw new IllegalArgumentException("Sua build precisa ter componentes de pelo menos 3 categorias.");
}
```

### Regra 2 — Obrigatoriedade de Fonte de Alimentação

Toda build deve obrigatoriamente conter pelo menos uma Fonte de Alimentação.

**Justificativa:** Sem uma fonte, nenhum computador pode funcionar. A ausência é detectada verificando se a capacidade total de fontes na build é zero.

**Implementação:**
```java
int capacidadeFonte = build.getItens().stream()
        .filter(item -> item.getComponente().getCategoria().getNome().equalsIgnoreCase("Fonte"))
        .mapToInt(item -> item.getComponente().getTdpWatts() * item.getQuantidade())
        .sum();

if (capacidadeFonte == 0) {
    throw new IllegalArgumentException("Sua montagem precisa de uma Fonte de alimentação.");
}
```

### Regra 3 — Compatibilidade Energética (limite de 80%)

O consumo total dos componentes não pode ultrapassar 80% da capacidade da fonte escolhida.

**Justificativa:** A margem de segurança de 20% é uma recomendação técnica padrão da indústria. Fontes operando acima de 80% da carga têm vida útil reduzida, menor eficiência energética e risco de instabilidade.

**Implementação:**
```java
int consumoTotal = build.getItens().stream()
        .filter(item -> !item.getComponente().getCategoria().getNome().equalsIgnoreCase("Fonte"))
        .mapToInt(ItemConfiguracao::getTdpTotal)
        .sum();

int limiteSeguro = (int) (capacidadeFonte * 0.80);

if (consumoTotal > limiteSeguro) {
    throw new IllegalArgumentException(
        "A fonte não suporta esta configuração. Consumo: " + consumoTotal +
        "W. Limite seguro (80%): " + limiteSeguro + "W."
    );
}
```

---

## 5. Dashboard

O dashboard é a tela principal do sistema, acessível após o login. Apresenta indicadores visuais e resumos organizados com Bootstrap 5.

### 5.1 Cards de Indicadores

Quatro cards no topo da página exibem os principais indicadores do sistema em tempo real:

| Card           | Dado Exibido                                     |
|----------------|--------------------------------------------------|
| Minhas Builds  | Quantidade de builds salvas pelo usuário logado  |
| Componentes    | Total de componentes disponíveis no catálogo     |
| Categorias     | Total de categorias de componentes               |
| Investido      | Soma do valor total de todas as builds do usuário|

### 5.2 Formulário de Nova Build

Painel lateral esquerdo com:
- Campo de texto para nomear a build
- Tabela com todas as categorias disponíveis
- Para cada categoria: um `<select>` com todos os componentes da categoria, exibindo fabricante, nome e consumo em Watts
- Campo numérico de quantidade por componente (mínimo 1, máximo 4)
- Botão de submissão que aciona as 3 regras de validação antes de salvar
- Caixa destacada em amarelo com as regras de validação visíveis para o usuário

### 5.3 Lista de Builds Salvas

Painel lateral direito com:
- Cada build exibida como um card expansível (accordion)
- Cabeçalho mostra o nome da build e o valor total formatado
- Ao expandir: barra visual de consumo energético, lista de componentes com quantidade e preço total por item
- Botões de ação: Editar (abre formulário de edição) e Excluir (com confirmação)

### 5.4 Alertas de Feedback

Mensagens de sucesso (verde) ou erro (vermelho) são exibidas no topo da página após cada ação, informando o resultado da operação. As mensagens de erro das regras de negócio são descritivas, indicando o consumo atual e o limite permitido.

---

## 6. Tecnologias Utilizadas

| Tecnologia          | Versão  | Finalidade                              |
|---------------------|---------|-----------------------------------------|
| Java                | 17      | Linguagem principal                     |
| Spring Boot         | 3.2.5   | Framework principal                     |
| Spring Web (MVC)    | 3.2.5   | Camada de controle HTTP                 |
| Spring Data JPA     | 3.2.5   | Persistência e acesso ao banco          |
| Spring Security     | 6.2.4   | Autenticação e controle de acesso       |
| Hibernate           | 6.4.4   | Implementação JPA / ORM                 |
| H2 Database         | 2.2.224 | Banco de dados em memória               |
| Thymeleaf           | 3.1.2   | Motor de templates HTML                 |
| Bootstrap           | 5.3.2   | Framework CSS e componentes visuais     |
| Bootstrap Icons     | 1.11.3  | Ícones da interface                     |
| BCrypt              | —       | Algoritmo de hash de senhas             |
| JUnit 5             | 5.x     | Framework de testes unitários           |
| Mockito             | 5.x     | Criação de mocks para testes            |
| Maven               | 3.x     | Gerenciamento de dependências e build   |

---

## 7. Arquitetura do Sistema

O projeto segue a arquitetura em camadas do Spring MVC:

```
src/main/java/br/edu/eseg/pcbuilder/
├── config/
│   ├── DataInitializer.java     — Cria usuários padrão ao iniciar
│   └── SecurityConfig.java      — Configuração de autenticação e rotas
├── controller/
│   └── PcBuilderController.java — Recebe requisições HTTP e retorna views
├── model/
│   ├── Categoria.java           — Entidade categoria
│   ├── Componente.java          — Entidade componente de hardware
│   ├── Configuracao.java        — Entidade build (configuração de PC)
│   ├── Fabricante.java          — Entidade fabricante
│   ├── ItemConfiguracao.java    — Classe de relacionamento N:M
│   └── Usuario.java             — Entidade usuário (implementa UserDetails)
├── repository/
│   ├── CategoriaRepository.java
│   ├── ComponenteRepository.java
│   ├── ConfiguracaoRepository.java
│   ├── FabricanteRepository.java
│   ├── ItemConfiguracaoRepository.java
│   └── UsuarioRepository.java
├── service/
│   ├── ConfiguracaoService.java     — Regras de negócio e operações de build
│   └── UserDetailsServiceImpl.java  — Integração Spring Security com banco
└── PcbuilderApplication.java        — Ponto de entrada da aplicação

src/main/resources/
├── templates/
│   ├── index.html          — Dashboard principal
│   ├── editar-build.html   — Formulário de edição de build
│   └── login.html          — Tela de login
├── application.properties  — Configurações da aplicação
└── data.sql                — Dados iniciais (fabricantes, categorias, componentes)

src/test/java/br/edu/eseg/pcbuilder/service/
└── ConfiguracaoServiceTest.java — Testes unitários das regras de negócio
```

---

## 8. Como Executar

### Pré-requisitos

- Java 17 ou superior instalado
- IntelliJ IDEA (Community ou Ultimate)
- Conexão com a internet para download das dependências Maven (apenas na primeira execução)

### Passos

1. Abra o IntelliJ IDEA e selecione **Open**
2. Navegue até a pasta do projeto e clique em **OK**
3. Clique em **Trust Project** quando solicitado
4. Aguarde o Maven baixar as dependências (barra de progresso no canto inferior)
5. Localize a classe `PcbuilderApplication.java` em `src/main/java/br/edu/eseg/pcbuilder/`
6. Clique com o botão direito → **Run 'PcbuilderApplication.main()'**
7. Aguarde a mensagem `Started PcbuilderApplication` no console
8. Acesse no navegador: **http://localhost:8080**

### Console do Banco de Dados H2 (opcional)

Durante a execução, o banco pode ser inspecionado em:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:pcbuilderdb`
- User: `sa`
- Password: (deixar em branco)

---

## 9. Acesso ao Sistema

Dois usuários são criados automaticamente ao iniciar a aplicação:

| Perfil | E-mail               | Senha  | Permissão  |
|--------|----------------------|--------|------------|
| Admin  | admin@eseg.edu.br    | 123456 | ROLE_ADMIN |
| Aluno  | aluno@eseg.edu.br    | 123456 | ROLE_USER  |

Cada usuário visualiza e gerencia apenas suas próprias builds.

---

## Testes

Os testes unitários cobrem as 3 regras de negócio do `ConfiguracaoService`:

| Teste                                              | Regra Validada       |
|----------------------------------------------------|----------------------|
| `salvarBuild_deveLancarExcecao_quandoMenosDeTresCategorias` | Regra 1 |
| `salvarBuild_deveLancarExcecao_quandoSemFonte`              | Regra 2 |
| `salvarBuild_deveLancarExcecao_quandoFonteInsuficiente`     | Regra 3 |
| `salvarBuild_deveSalvarComSucesso_quandoTodaValidacaoPassar`| Caminho feliz |

Para executar os testes no IntelliJ: clique com botão direito em `ConfiguracaoServiceTest.java` → **Run 'ConfiguracaoServiceTest'**.
