package br.com.hybridIndexer.util;

import br.com.hybridIndexer.model.Transaction;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por ler arquivos CSV de transações
 */
public class CSVReader {
    
    private static final String CSV_SEPARATOR = ",";
    
    /**
     * Lê um arquivo CSV e retorna uma lista de transações
     * @param filePath Caminho do arquivo CSV
     * @return Lista de transações lidas do arquivo
     * @throws IOException Se houver erro na leitura do arquivo
     */
    public static List<Transaction> readTransactions(String filePath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Pular cabeçalho se existir
                if (firstLine) {
                    firstLine = false;
                    // Verificar se é cabeçalho
                    if (line.toLowerCase().contains("id") || 
                        line.toLowerCase().contains("valor") ||
                        line.toLowerCase().contains("origem")) {
                        continue;
                    }
                }
                
                Transaction transaction = parseTransaction(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        }
        
        return transactions;
    }
    
    /**
     * Converte uma linha CSV em um objeto Transaction
     * @param line Linha do CSV
     * @return Objeto Transaction ou null se houver erro
     */
    private static Transaction parseTransaction(String line) {
        try {
            // Remover espaços extras e dividir por vírgula
            String[] fields = line.trim().split(CSV_SEPARATOR);
            
            if (fields.length < 5) {
                System.err.println("Linha inválida (menos de 5 campos): " + line);
                return null;
            }
            
            // Extrair campos
            String id = fields[0].trim();
            float valor = Float.parseFloat(fields[1].trim());
            String origem = fields[2].trim();
            String destino = fields[3].trim();
            String timestamp = fields[4].trim();
            
            // Criar e retornar transação
            return new Transaction(id, valor, origem, destino, timestamp);
            
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter valor numérico na linha: " + line);
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + line);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Escreve transações em um arquivo CSV
     * @param transactions Lista de transações
     * @param filePath Caminho do arquivo de saída
     * @throws IOException Se houver erro na escrita
     */
    public static void writeTransactions(List<Transaction> transactions, String filePath) 
            throws IOException {
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            // Escrever cabeçalho
            bw.write("id,valor,origem,destino,timestamp");
            bw.newLine();
            
            // Escrever transações
            for (Transaction t : transactions) {
                String line = String.format("%s,%.2f,%s,%s,%s",
                    t.getId(),
                    t.getValor(),
                    t.getOrigem(),
                    t.getDestino(),
                    t.getTimestamp()
                );
                bw.write(line);
                bw.newLine();
            }
        }
    }
    
    /**
     * Conta o número de linhas em um arquivo CSV (excluindo cabeçalho)
     * @param filePath Caminho do arquivo
     * @return Número de linhas de dados
     * @throws IOException Se houver erro na leitura
     */
    public static int countLines(String filePath) throws IOException {
        int count = 0;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    // Verificar se é cabeçalho
                    if (line.toLowerCase().contains("id") || 
                        line.toLowerCase().contains("valor") ||
                        line.toLowerCase().contains("origem")) {
                        continue;
                    }
                }
                count++;
            }
        }
        
        return count;
    }
}
