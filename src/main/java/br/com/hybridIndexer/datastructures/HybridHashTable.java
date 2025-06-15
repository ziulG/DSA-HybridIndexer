package br.com.hybridIndexer.datastructures;

import br.com.hybridIndexer.model.Transaction;
import java.util.*;

/**
 * Tabela Hash Híbrida que indexa transações por dois campos (id e origem)
 * com diferentes estratégias de tratamento de colisão
 */
public class HybridHashTable {
    
    // Tipos de estruturas que podem estar em cada posição da tabela
    private enum EntryType {
        EMPTY,
        TRANSACTION,        // Transação única (para origem com sondagem quadrática)
        LINKED_LIST,        // Lista encadeada (para id)
        AVL_TREE,          // Árvore AVL (para origem após muitas colisões)
        RED_BLACK_TREE     // Árvore Rubro-Negra (para origem após AVL ficar muito alta)
    }
    
    // Classe interna para armazenar entrada na tabela
    private class TableEntry {
        EntryType type;
        Object data;
        
        TableEntry() {
            this.type = EntryType.EMPTY;
            this.data = null;
        }
    }
    
    private TableEntry[] table;
    private int size;
    private int capacity;
    private static final int INITIAL_CAPACITY = 512;
    private static final double LOAD_FACTOR = 0.75;
    private static final int MAX_QUADRATIC_PROBES = 3;
    private static final int MAX_AVL_HEIGHT = 10;
    
    // Contadores para análise de desempenho
    private long comparisons;
    private long assignments;
    
    /**
     * Construtor padrão
     */
    public HybridHashTable() {
        this(INITIAL_CAPACITY);
    }
    
    /**
     * Construtor com capacidade inicial
     */
    public HybridHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new TableEntry[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new TableEntry();
        }
        this.size = 0;
        this.comparisons = 0;
        this.assignments = 0;
    }
    
    /**
     * Insere uma transação na tabela, indexando por id e origem
     */
    public void put(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transação não pode ser null");
        }
        
        // Verificar se precisa redimensionar
        if (size >= capacity * LOAD_FACTOR) {
            resize(capacity * 2);
        }
        
        // Indexar por ID (usando encadeamento)
        indexById(transaction);
        
        // Indexar por origem (usando sondagem quadrática com escalonamento)
        indexByOrigem(transaction);
        
        size++;
    }
    
    /**
     * Indexa transação por ID usando encadeamento
     */
    private void indexById(Transaction transaction) {
        int index = hash(transaction.getId());
        TableEntry entry = table[index];
        
        comparisons++;
        
        if (entry.type == EntryType.EMPTY || entry.type == EntryType.TRANSACTION) {
            // Criar nova lista encadeada
            LinkedList<Transaction> list = new LinkedList<>();
            if (entry.type == EntryType.TRANSACTION) {
                // Adicionar transação existente à lista
                list.add((Transaction) entry.data);
                assignments++;
            }
            list.add(transaction);
            entry.type = EntryType.LINKED_LIST;
            entry.data = list;
            assignments += 2;
        } else if (entry.type == EntryType.LINKED_LIST) {
            // Adicionar à lista existente
            @SuppressWarnings("unchecked")
            LinkedList<Transaction> list = (LinkedList<Transaction>) entry.data;
            list.add(transaction);
            assignments++;
        }
        // Nota: Se for AVL ou RB, é porque foi indexado por origem, não id
    }
    
    /**
     * Indexa transação por origem usando sondagem quadrática com escalonamento
     */
    private void indexByOrigem(Transaction transaction) {
        String origem = transaction.getOrigem();
        int baseIndex = hash(origem);
        int collisions = 0;
        
        // Tentar inserir com sondagem quadrática
        for (int i = 0; i <= MAX_QUADRATIC_PROBES; i++) {
            int index = (baseIndex + i * i) % capacity;
            TableEntry entry = table[index];
            
            comparisons++;
            
            if (entry.type == EntryType.EMPTY) {
                // Posição vazia, inserir diretamente
                entry.type = EntryType.TRANSACTION;
                entry.data = transaction;
                assignments++;
                return;
            } else if (entry.type == EntryType.TRANSACTION) {
                Transaction existing = (Transaction) entry.data;
                if (existing.getOrigem().equals(origem)) {
                    // Mesma origem, converter para lista temporária e depois AVL
                    migrateToAVL(index, origem);
                    // Agora é uma AVL, inserir nela
                    AVLTree<Transaction> avl = (AVLTree<Transaction>) table[index].data;
                    avl.insert(transaction);
                    checkAVLHeight(index);
                    return;
                }
                // Origem diferente, continuar procurando
                collisions++;
            } else if (entry.type == EntryType.AVL_TREE || entry.type == EntryType.RED_BLACK_TREE) {
                // Verificar se é a origem correta
                if (isCorrectOrigemTree(entry, origem)) {
                    insertIntoTree(entry, transaction);
                    if (entry.type == EntryType.AVL_TREE) {
                        checkAVLHeight(index);
                    }
                    return;
                }
                collisions++;
            }
            // LINKED_LIST só existe para id, não origem
        }
        
        // Excedeu o limite de colisões, migrar para AVL
        int finalIndex = (baseIndex + MAX_QUADRATIC_PROBES * MAX_QUADRATIC_PROBES) % capacity;
        migrateOrigemToAVL(origem, transaction);
    }
    
    /**
     * Migra registros de uma origem para uma Árvore AVL
     */
    private void migrateToAVL(int index, String origem) {
        AVLTree<Transaction> avl = new AVLTree<>();
        TableEntry entry = table[index];
        
        if (entry.type == EntryType.TRANSACTION) {
            Transaction existing = (Transaction) entry.data;
            if (existing.getOrigem().equals(origem)) {
                avl.insert(existing);
                assignments++;
            }
        }
        
        entry.type = EntryType.AVL_TREE;
        entry.data = avl;
        assignments++;
    }
    
    /**
     * Migra todos os registros de uma origem para AVL após muitas colisões
     */
    private void migrateOrigemToAVL(String origem, Transaction newTransaction) {
        // Coletar todas as transações dessa origem
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(newTransaction);
        
        // Procurar em toda a tabela por transações dessa origem
        for (int i = 0; i < capacity; i++) {
            TableEntry entry = table[i];
            if (entry.type == EntryType.TRANSACTION) {
                Transaction t = (Transaction) entry.data;
                if (t.getOrigem().equals(origem)) {
                    transactions.add(t);
                    // Limpar entrada
                    entry.type = EntryType.EMPTY;
                    entry.data = null;
                    assignments++;
                }
            }
        }
        
        // Criar AVL e inserir todas as transações
        AVLTree<Transaction> avl = new AVLTree<>();
        for (Transaction t : transactions) {
            avl.insert(t);
            assignments++;
        }
        
        // Colocar AVL no índice base
        int index = hash(origem);
        table[index].type = EntryType.AVL_TREE;
        table[index].data = avl;
        assignments++;
    }
    
    /**
     * Verifica se uma árvore é da origem correta
     */
    private boolean isCorrectOrigemTree(TableEntry entry, String origem) {
        // Para verificar, precisamos olhar qualquer elemento da árvore
        if (entry.type == EntryType.AVL_TREE) {
            AVLTree<Transaction> avl = (AVLTree<Transaction>) entry.data;
            if (!avl.isEmpty()) {
                // Usar o primeiro elemento encontrado para verificar
                final boolean[] found = {false};
                avl.inOrderTraversal(t -> {
                    if (!found[0]) {
                        found[0] = t.getOrigem().equals(origem);
                    }
                });
                return found[0];
            }
        } else if (entry.type == EntryType.RED_BLACK_TREE) {
            RedBlackTree<Transaction> rb = (RedBlackTree<Transaction>) entry.data;
            if (!rb.isEmpty()) {
                final boolean[] found = {false};
                rb.inOrderTraversal(t -> {
                    if (!found[0]) {
                        found[0] = t.getOrigem().equals(origem);
                    }
                });
                return found[0];
            }
        }
        return false;
    }
    
    /**
     * Insere uma transação em uma árvore
     */
    private void insertIntoTree(TableEntry entry, Transaction transaction) {
        if (entry.type == EntryType.AVL_TREE) {
            AVLTree<Transaction> avl = (AVLTree<Transaction>) entry.data;
            avl.insert(transaction);
            assignments++;
        } else if (entry.type == EntryType.RED_BLACK_TREE) {
            RedBlackTree<Transaction> rb = (RedBlackTree<Transaction>) entry.data;
            rb.insert(transaction);
            assignments++;
        }
    }
    
    /**
     * Verifica altura da AVL e converte para Rubro-Negra se necessário
     */
    private void checkAVLHeight(int index) {
        TableEntry entry = table[index];
        if (entry.type == EntryType.AVL_TREE) {
            AVLTree<Transaction> avl = (AVLTree<Transaction>) entry.data;
            if (avl.getHeight() > MAX_AVL_HEIGHT) {
                // Converter para Rubro-Negra
                RedBlackTree<Transaction> rb = new RedBlackTree<>();
                avl.inOrderTraversal(rb::insert);
                entry.type = EntryType.RED_BLACK_TREE;
                entry.data = rb;
                assignments++;
            }
        }
    }
    
    /**
     * Busca transações por origem dentro de um intervalo de tempo
     */
    public List<Transaction> search(String origem, String startDate, String endDate) {
        List<Transaction> result = new ArrayList<>();
        int baseIndex = hash(origem);
        
        // Procurar com sondagem quadrática
        for (int i = 0; i <= MAX_QUADRATIC_PROBES; i++) {
            int index = (baseIndex + i * i) % capacity;
            TableEntry entry = table[index];
            
            comparisons++;
            
            if (entry.type == EntryType.EMPTY) {
                continue;
            } else if (entry.type == EntryType.TRANSACTION) {
                Transaction t = (Transaction) entry.data;
                if (t.getOrigem().equals(origem) && 
                    isInTimeRange(t.getTimestamp(), startDate, endDate)) {
                    result.add(t);
                }
            } else if (entry.type == EntryType.AVL_TREE || entry.type == EntryType.RED_BLACK_TREE) {
                if (isCorrectOrigemTree(entry, origem)) {
                    collectFromTree(entry, result, startDate, endDate);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Coleta transações de uma árvore dentro do intervalo de tempo
     */
    private void collectFromTree(TableEntry entry, List<Transaction> result, 
                                String startDate, String endDate) {
        if (entry.type == EntryType.AVL_TREE) {
            AVLTree<Transaction> avl = (AVLTree<Transaction>) entry.data;
            avl.inOrderTraversal(t -> {
                if (isInTimeRange(t.getTimestamp(), startDate, endDate)) {
                    result.add(t);
                    comparisons++;
                }
            });
        } else if (entry.type == EntryType.RED_BLACK_TREE) {
            RedBlackTree<Transaction> rb = (RedBlackTree<Transaction>) entry.data;
            rb.inOrderTraversal(t -> {
                if (isInTimeRange(t.getTimestamp(), startDate, endDate)) {
                    result.add(t);
                    comparisons++;
                }
            });
        }
    }
    
    /**
     * Verifica se um timestamp está dentro do intervalo
     */
    private boolean isInTimeRange(String timestamp, String startDate, String endDate) {
        comparisons += 2;
        return timestamp.compareTo(startDate) >= 0 && timestamp.compareTo(endDate) <= 0;
    }
    
    /**
     * Função hash simples
     */
    private int hash(String key) {
        return Math.abs(key.hashCode()) % capacity;
    }
    
    /**
     * Redimensiona a tabela
     */
    private void resize(int newCapacity) {
        TableEntry[] oldTable = table;
        table = new TableEntry[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            table[i] = new TableEntry();
        }
        
        int oldCapacity = capacity;
        capacity = newCapacity;
        size = 0;
        
        // Re-inserir todos os elementos
        for (int i = 0; i < oldCapacity; i++) {
            TableEntry entry = oldTable[i];
            if (entry.type == EntryType.TRANSACTION) {
                put((Transaction) entry.data);
            } else if (entry.type == EntryType.LINKED_LIST) {
                @SuppressWarnings("unchecked")
                LinkedList<Transaction> list = (LinkedList<Transaction>) entry.data;
                for (Transaction t : list) {
                    put(t);
                }
            } else if (entry.type == EntryType.AVL_TREE) {
                AVLTree<Transaction> avl = (AVLTree<Transaction>) entry.data;
                avl.inOrderTraversal(this::put);
            } else if (entry.type == EntryType.RED_BLACK_TREE) {
                RedBlackTree<Transaction> rb = (RedBlackTree<Transaction>) entry.data;
                rb.inOrderTraversal(this::put);
            }
        }
    }
    
    // Métodos para análise de desempenho
    public long getComparisons() {
        return comparisons;
    }
    
    public long getAssignments() {
        return assignments;
    }
    
    public void resetCounters() {
        comparisons = 0;
        assignments = 0;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Imprime estatísticas da tabela para debug
     */
    public void printStatistics() {
        int empty = 0, transactions = 0, lists = 0, avls = 0, rbs = 0;
        
        for (TableEntry entry : table) {
            switch (entry.type) {
                case EMPTY: empty++; break;
                case TRANSACTION: transactions++; break;
                case LINKED_LIST: lists++; break;
                case AVL_TREE: avls++; break;
                case RED_BLACK_TREE: rbs++; break;
            }
        }
        
        System.out.println("=== Estatísticas da Tabela Hash Híbrida ===");
        System.out.println("Capacidade: " + capacity);
        System.out.println("Tamanho: " + size);
        System.out.println("Fator de carga: " + String.format("%.2f", (double)size/capacity));
        System.out.println("Posições vazias: " + empty);
        System.out.println("Transações individuais: " + transactions);
        System.out.println("Listas encadeadas: " + lists);
        System.out.println("Árvores AVL: " + avls);
        System.out.println("Árvores Rubro-Negras: " + rbs);
        System.out.println("Comparações totais: " + comparisons);
        System.out.println("Atribuições totais: " + assignments);
    }
}
