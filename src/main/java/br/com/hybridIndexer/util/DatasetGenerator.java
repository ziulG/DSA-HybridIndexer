package main.java.br.com.hybridIndexer.util;

import main.java.br.com.hybridIndexer.model.Transaction;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Classe para gerar datasets de teste com transações financeiras
 */
public class DatasetGenerator {
    
    private static final Random random = new Random();
    
    // Listas de valores possíveis
    private static final String[] ORIGENS = {
        "BancoCentral", "BancoNacional", "BancoInternacional", "BancoDigital", "BancoRegional",
        "Fintech1", "Fintech2", "Fintech3", "CooperativaA", "CooperativaB",
        "InstitutoFinanceiro", "AgenciaCredito", "CasaCambio", "BancoInvestimento", "BancoComercial"
    };
    
    private static final String[] DESTINOS = {
        "ClientePF001", "ClientePF002", "ClientePF003", "ClientePF004", "ClientePF005",
        "ClientePJ001", "ClientePJ002", "ClientePJ003", "ClientePJ004", "ClientePJ005",
        "Fornecedor01", "Fornecedor02", "Fornecedor03", "Parceiro01", "Parceiro02"
    };
    
    /**
     * Gera um arquivo CSV com transações aleatórias
     * @param filename Nome do arquivo de saída
     * @param numberOfTransactions Número de transações a gerar
     * @throws IOException Se houver erro na escrita do arquivo
     */
    public static void generateDataset(String filename, int numberOfTransactions) throws IOException {
        System.out.println("Gerando dataset com " + numberOfTransactions + " transações...");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Escrever cabeçalho
            writer.write("id,valor,origem,destino,timestamp");
            writer.newLine();
            
            // Gerar transações
            for (int i = 0; i < numberOfTransactions; i++) {
                Transaction transaction = generateRandomTransaction(i);
                writeTransaction(writer, transaction);
                
                // Mostrar progresso
                if ((i + 1) % 10000 == 0) {
                    System.out.println("  " + (i + 1) + " transações geradas...");
                }
            }
        }
        
        System.out.println("✓ Dataset gerado com sucesso: " + filename);
    }
    
    /**
     * Gera um dataset com distribuição específica para testar colisões
     * @param filename Nome do arquivo de saída
     * @param numberOfTransactions Número total de transações
     * @param collisionRate Taxa de colisão desejada (0.0 a 1.0)
     * @throws IOException Se houver erro na escrita do arquivo
     */
    public static void generateDatasetWithCollisions(String filename, int numberOfTransactions, 
                                                   double collisionRate) throws IOException {
        System.out.println("Gerando dataset com taxa de colisão de " + (collisionRate * 100) + "%...");
        
        // Calcular número de origens únicas baseado na taxa de colisão
        int uniqueOrigins = Math.max(1, (int)(numberOfTransactions * (1 - collisionRate)));
        String[] limitedOrigens = new String[Math.min(uniqueOrigins, ORIGENS.length)];
        
        // Selecionar origens limitadas
        for (int i = 0; i < limitedOrigens.length; i++) {
            limitedOrigens[i] = ORIGENS[i % ORIGENS.length];
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Escrever cabeçalho
            writer.write("id,valor,origem,destino,timestamp");
            writer.newLine();
            
            // Gerar transações
            for (int i = 0; i < numberOfTransactions; i++) {
                Transaction transaction = generateTransactionWithLimitedOrigins(i, limitedOrigens);
                writeTransaction(writer, transaction);
                
                if ((i + 1) % 10000 == 0) {
                    System.out.println("  " + (i + 1) + " transações geradas...");
                }
            }
        }
        
        System.out.println("✓ Dataset com colisões gerado: " + filename);
    }
    
    /**
     * Gera uma transação aleatória
     */
    private static Transaction generateRandomTransaction(int id) {
        String transactionId = String.format("TRX%08d", id);
        float valor = 0.01f + random.nextFloat() * 99999.99f; // Valor entre 0.01 e 100000.00
        String origem = ORIGENS[random.nextInt(ORIGENS.length)];
        String destino = DESTINOS[random.nextInt(DESTINOS.length)];
        String timestamp = generateRandomTimestamp();
        
        return new Transaction(transactionId, valor, origem, destino, timestamp);
    }
    
    /**
     * Gera uma transação com origens limitadas (para aumentar colisões)
     */
    private static Transaction generateTransactionWithLimitedOrigins(int id, String[] limitedOrigens) {
        String transactionId = String.format("TRX%08d", id);
        float valor = 0.01f + random.nextFloat() * 99999.99f;
        String origem = limitedOrigens[random.nextInt(limitedOrigens.length)];
        String destino = DESTINOS[random.nextInt(DESTINOS.length)];
        String timestamp = generateRandomTimestamp();
        
        return new Transaction(transactionId, valor, origem, destino, timestamp);
    }
    
    /**
     * Gera um timestamp aleatório no formato YYYY-MM-DD HH:MM:SS
     */
    private static String generateRandomTimestamp() {
        int year = 2020 + random.nextInt(5); // 2020-2024
        int month = 1 + random.nextInt(12);  // 1-12
        int day = 1 + random.nextInt(28);    // 1-28 (simplificado)
        int hour = random.nextInt(24);       // 0-23
        int minute = random.nextInt(60);     // 0-59
        int second = random.nextInt(60);     // 0-59
        
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", 
                           year, month, day, hour, minute, second);
    }
    
    /**
     * Escreve uma transação no arquivo CSV
     */
    private static void writeTransaction(BufferedWriter writer, Transaction transaction) 
            throws IOException {
        String line = String.format("%s,%.2f,%s,%s,%s",
            transaction.getId(),
            transaction.getValor(),
            transaction.getOrigem(),
            transaction.getDestino(),
            transaction.getTimestamp()
        );
        writer.write(line);
        writer.newLine();
    }
    
    /**
     * Método main para gerar datasets de teste
     */
    public static void main(String[] args) {
        try {
            // Gerar dataset pequeno
            generateDataset("transacoes_pequeno.csv", 1000);
            
            // Gerar dataset médio
            generateDataset("transacoes_medio.csv", 10000);
            
            // Gerar dataset grande
            generateDataset("transacoes_grande.csv", 100000);
            
            // Gerar dataset com alta taxa de colisão
            generateDatasetWithCollisions("transacoes_colisao_alta.csv", 10000, 0.8);
            
            System.out.println("\nTodos os datasets foram gerados com sucesso!");
            
        } catch (IOException e) {
            System.err.println("Erro ao gerar datasets: " + e.getMessage());
        }
    }
}