package br.com.hybridIndexer;

import br.com.hybridIndexer.datastructures.HybridHashTable;
import br.com.hybridIndexer.model.Transaction;
import br.com.hybridIndexer.util.CSVReader;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal do sistema de indexação híbrida
 */
public class Main {
    
    private static HybridHashTable hashTable;
    private static List<Transaction> transactions;
    
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  Sistema de Indexação Híbrida");
        System.out.println("  Estrutura de Dados II - UFMA");
        System.out.println("===========================================\n");
        
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            printMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consumir newline
            
            switch (choice) {
                case 1:
                    loadDataset(scanner);
                    break;
                case 2:
                    runPerformanceTests();
                    break;
                case 3:
                    searchTransactions(scanner);
                    break;
                case 4:
                    printStatistics();
                    break;
                case 5:
                    running = false;
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
        
        scanner.close();
    }
    
    private static void printMenu() {
        System.out.println("\n=== MENU PRINCIPAL ===");
        System.out.println("1. Carregar dataset de transações");
        System.out.println("2. Executar testes de performance");
        System.out.println("3. Buscar transações por origem e período");
        System.out.println("4. Exibir estatísticas da tabela hash");
        System.out.println("5. Sair");
        System.out.print("Escolha uma opção: ");
    }
    
    private static void loadDataset(Scanner scanner) {
        System.out.print("Digite o caminho do arquivo CSV: ");
        String filePath = scanner.nextLine();
        
        try {
            System.out.println("Carregando transações...");
            long startTime = System.currentTimeMillis();
            
            transactions = CSVReader.readTransactions(filePath);
            hashTable = new HybridHashTable();
            
            // Inserir transações na tabela hash
            for (Transaction transaction : transactions) {
                hashTable.put(transaction);
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            
            System.out.println("✓ Dataset carregado com sucesso!");
            System.out.println("  - Total de transações: " + transactions.size());
            System.out.println("  - Tempo de carregamento: " + elapsedTime + " ms");
            System.out.println("  - Tamanho da tabela hash: " + hashTable.getSize());
            
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }
    
    private static void runPerformanceTests() {
        if (hashTable == null || transactions == null) {
            System.out.println("Por favor, carregue um dataset primeiro!");
            return;
        }
        
        System.out.println("\n=== TESTES DE PERFORMANCE ===\n");
        
        // Teste 1: Inserção
        testInsertion();
        
        // Teste 2: Busca
        testSearch();
        
        // Teste 3: Análise de estruturas
        analyzeStructures();
    }
    
    private static void testInsertion() {
        System.out.println("1. TESTE DE INSERÇÃO");
        System.out.println("--------------------");
        
        // Criar nova tabela para teste limpo
        HybridHashTable testTable = new HybridHashTable();
        
        // Testar com diferentes tamanhos
        int[] sizes = {100, 1000, 5000, 10000};
        
        System.out.println("Tamanho | Tempo (ms) | Comparações | Atribuições | Comp/Op | Atrib/Op");
        System.out.println("--------|------------|-------------|-------------|---------|----------");
        
        for (int size : sizes) {
            if (size > transactions.size()) {
                break;
            }
            
            testTable = new HybridHashTable();
            testTable.resetCounters();
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < size; i++) {
                testTable.put(transactions.get(i));
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            long comparisons = testTable.getComparisons();
            long assignments = testTable.getAssignments();
            
            double compPerOp = (double) comparisons / size;
            double assignPerOp = (double) assignments / size;
            
            System.out.printf("%-7d | %-10d | %-11d | %-11d | %-7.2f | %-8.2f%n",
                size, elapsedTime, comparisons, assignments, compPerOp, assignPerOp);
        }
        System.out.println();
    }
    
    private static void testSearch() {
        System.out.println("2. TESTE DE BUSCA");
        System.out.println("-----------------");
        
        if (transactions.isEmpty()) {
            System.out.println("Sem transações para testar!");
            return;
        }
        
        // Selecionar algumas origens para teste
        String[] testOrigins = new String[Math.min(5, transactions.size())];
        for (int i = 0; i < testOrigins.length; i++) {
            testOrigins[i] = transactions.get(i * (transactions.size() / testOrigins.length)).getOrigem();
        }
        
        System.out.println("Origem | Resultados | Tempo (ms) | Comparações");
        System.out.println("-------|------------|------------|-------------");
        
        for (String origem : testOrigins) {
            hashTable.resetCounters();
            
            long startTime = System.currentTimeMillis();
            List<Transaction> results = hashTable.search(origem, "2024-01-01", "2024-12-31");
            long endTime = System.currentTimeMillis();
            
            long elapsedTime = endTime - startTime;
            long comparisons = hashTable.getComparisons();
            
            System.out.printf("%-6s | %-10d | %-10d | %-11d%n",
                origem.length() > 6 ? origem.substring(0, 6) : origem,
                results.size(), elapsedTime, comparisons);
        }
        System.out.println();
    }
    
    private static void analyzeStructures() {
        System.out.println("3. ANÁLISE DE ESTRUTURAS");
        System.out.println("------------------------");
        hashTable.printStatistics();
    }
    
    private static void searchTransactions(Scanner scanner) {
        if (hashTable == null) {
            System.out.println("Por favor, carregue um dataset primeiro!");
            return;
        }
        
        System.out.print("Digite a origem: ");
        String origem = scanner.nextLine();
        
        System.out.print("Digite a data inicial (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();
        
        System.out.print("Digite a data final (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();
        
        hashTable.resetCounters();
        long startTime = System.currentTimeMillis();
        
        List<Transaction> results = hashTable.search(origem, startDate, endDate);
        
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        
        System.out.println("\n=== RESULTADOS DA BUSCA ===");
        System.out.println("Origem: " + origem);
        System.out.println("Período: " + startDate + " até " + endDate);
        System.out.println("Transações encontradas: " + results.size());
        System.out.println("Tempo de busca: " + elapsedTime + " ms");
        System.out.println("Comparações realizadas: " + hashTable.getComparisons());
        
        if (results.size() > 0) {
            System.out.println("\nPrimeiras 10 transações:");
            System.out.println("ID | Valor | Destino | Timestamp");
            System.out.println("---|-------|---------|----------");
            
            int count = 0;
            for (Transaction t : results) {
                if (count++ >= 10) break;
                System.out.printf("%-3s | %-7.2f | %-7s | %s%n",
                    t.getId().length() > 3 ? t.getId().substring(0, 3) : t.getId(),
                    t.getValor(),
                    t.getDestino().length() > 7 ? t.getDestino().substring(0, 7) : t.getDestino(),
                    t.getTimestamp());
            }
        }
    }
    
    private static void printStatistics() {
        if (hashTable == null) {
            System.out.println("Por favor, carregue um dataset primeiro!");
            return;
        }
        
        hashTable.printStatistics();
    }
    
    /**
     * Gera um dataset de teste se necessário
     */
    private static void generateTestDataset(String filename, int size) {
        try {
            List<Transaction> testTransactions = new java.util.ArrayList<>();
            java.util.Random random = new java.util.Random();
            
            String[] origens = {"Banco1", "Banco2", "Banco3", "Banco4", "Banco5"};
            String[] destinos = {"Cliente1", "Cliente2", "Cliente3", "Cliente4", "Cliente5"};
            
            for (int i = 0; i < size; i++) {
                String id = "T" + String.format("%06d", i);
                float valor = random.nextFloat() * 10000;
                String origem = origens[random.nextInt(origens.length)];
                String destino = destinos[random.nextInt(destinos.length)];
                String timestamp = String.format("2024-%02d-%02d", 
                    random.nextInt(12) + 1, random.nextInt(28) + 1);
                
                testTransactions.add(new Transaction(id, valor, origem, destino, timestamp));
            }
            
            CSVReader.writeTransactions(testTransactions, filename);
            System.out.println("Dataset de teste gerado: " + filename);
            
        } catch (IOException e) {
            System.err.println("Erro ao gerar dataset: " + e.getMessage());
        }
    }
}
