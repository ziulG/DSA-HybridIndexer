# DSA-HybridIndexer

Sistema de Indexação Híbrida para Transações Financeiras desenvolvido para a disciplina de Estrutura de Dados II da UFMA.

## 📋 Descrição do Projeto

Este projeto implementa um indexador híbrido que utiliza uma única Tabela Hash com múltiplos comportamentos de tratamento de colisão. A estrutura é capaz de evoluir dinamicamente para Árvores Balanceadas (AVL e Rubro-Negra) sob condições específicas, otimizando o desempenho conforme o volume e padrão dos dados.

### Características Principais:

- **Indexação Dupla**: Cada transação é indexada por dois campos (`id` e `origem`)
- **Tratamento de Colisão Diferenciado**:
  - Por `id`: Encadeamento (lista ligada)
  - Por `origem`: Sondagem quadrática com escalonamento para árvores
- **Evolução Dinâmica**:
  - Após 3 colisões na sondagem quadrática → migra para Árvore AVL
  - Quando altura da AVL > 10 → converte para Árvore Rubro-Negra
- **Busca por Intervalo**: Permite buscar transações por origem dentro de um período

## 🏗️ Estrutura do Projeto

```
DSA-HybridIndexer/
├── scripts/
│   ├── compile.sh                                     # Script de compilação
│   ├── run.sh                                         # Script de execução
│   └── generate-datasets.sh                           # Script para gerar datasets
├── src/
│   └── main/
│       └── java/
│           └── br/
│               └── com/
│                   └── hybridIndexer/
│                       ├── Main.java                    # Classe principal
│                       ├── model/
│                       │   └── Transaction.java         # Modelo de transação
│                       ├── datastructures/
│                       │   ├── HybridHashTable.java    # Tabela hash híbrida
│                       │   ├── BalancedTree.java       # Interface para árvores
│                       │   ├── AVLTree.java            # Implementação AVL
│                       │   ├── RedBlackTree.java       # Implementação RB
│                       │   ├── NodeAVL.java            # Nó da árvore AVL
│                       │   └── NodeRB.java             # Nó da árvore RB
│                       └── util/
│                           ├── CSVReader.java          # Leitor de CSV
│                           └── DatasetGenerator.java   # Gerador de dados
├── pom.xml                                            # Configuração Maven
└── README.md                                          # Este arquivo
```

## 🔧 Pré-requisitos

- Java JDK 11 ou superior
- Maven 3.6 ou superior (opcional, mas recomendado)

## 🚀 Como Compilar e Executar

### Usando Maven (Recomendado)

1. **Compilar o projeto:**
```bash
mvn clean compile
```

2. **Gerar o JAR executável:**
```bash
mvn package
```

3. **Executar o programa:**
```bash
java -jar target/hybrid-indexer-1.0.0.jar
```

### Compilação Manual

1. **Compilar todos os arquivos:**
```bash
# Usando o script de compilação
./scripts/compile.sh

# Ou manualmente
javac -d out src/main/java/br/com/hybridIndexer/**/*.java
```

2. **Executar o programa:**
```bash
# Usando o script de execução
./scripts/run.sh

# Ou manualmente
java -cp out main.java.br.com.hybridIndexer.Main
```

## 📊 Gerando Datasets de Teste

O projeto inclui um gerador de datasets. Para criar arquivos de teste:

```bash
# Usando o script de geração
./scripts/generate-datasets.sh

# Usando Maven
mvn exec:java -Dexec.mainClass="main.java.br.com.hybridIndexer.util.DatasetGenerator"

# Ou diretamente
java -cp out main.java.br.com.hybridIndexer.util.DatasetGenerator
```

Isso criará os seguintes arquivos:
- `transacoes_pequeno.csv` (1.000 registros)
- `transacoes_medio.csv` (10.000 registros)
- `transacoes_grande.csv` (100.000 registros)
- `transacoes_colisao_alta.csv` (10.000 registros com alta taxa de colisão)

## 💻 Usando o Sistema

Ao executar o programa, você verá um menu interativo:

```
===========================================
  Sistema de Indexação Híbrida
  Estrutura de Dados II - UFMA
===========================================

=== MENU PRINCIPAL ===
1. Carregar dataset de transações
2. Executar testes de performance
3. Buscar transações por origem e período
4. Exibir estatísticas da tabela hash
5. Sair
```

### Funcionalidades:

1. **Carregar Dataset**: Carrega um arquivo CSV de transações
2. **Testes de Performance**: Executa testes automatizados de inserção e busca
3. **Buscar Transações**: Permite buscar por origem e intervalo de datas
4. **Estatísticas**: Mostra informações detalhadas sobre a estrutura interna

## 📈 Análise de Performance

O sistema coleta as seguintes métricas:

- **Número de Comparações**: Quantas comparações de chaves foram feitas
- **Número de Atribuições**: Quantas movimentações de dados ocorreram
- **Tempo de Execução**: Tempo total para cada operação
- **Distribuição de Estruturas**: Quantas listas, AVLs e RBs foram criadas

## 🎯 Casos de Uso Específicos

### Testando Evolução para AVL
```csv
id,valor,origem,destino,timestamp
T001,100.00,BancoA,Cliente1,2024-01-01
T002,200.00,BancoA,Cliente2,2024-01-02
T003,300.00,BancoA,Cliente3,2024-01-03
T004,400.00,BancoA,Cliente4,2024-01-04
```
Após 4 inserções com mesma origem, a estrutura migrará para AVL.

### Testando Conversão para Rubro-Negra
Insira mais de 2000 transações com a mesma origem para forçar a altura da AVL > 10.

## 🐛 Resolução de Problemas

### Erro de Package
Se encontrar erros de package, verifique se a estrutura de diretórios está correta:
```
src/main/java/br/com/hybridIndexer/
```

### OutOfMemoryError
Para datasets muito grandes, aumente a memória da JVM:
```bash
java -Xmx2g -jar target/hybrid-indexer-1.0.0.jar
```

## 📚 Referências

- Cormen, T. H., et al. "Introduction to Algorithms" (Capítulos sobre Hash Tables e Árvores Balanceadas)
- Sedgewick, R., Wayne, K. "Algorithms, 4th Edition" (Implementações de AVL e Red-Black Trees)

## 👥 Autor

- Luiz Gustavo Bruzaca Cutrim

Desenvolvido para a disciplina de Estrutura de Dados II - UFMA

---