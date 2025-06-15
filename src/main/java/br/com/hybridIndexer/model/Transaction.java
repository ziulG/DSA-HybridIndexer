package main.java.br.com.hybridIndexer.model;

import java.util.Objects;

/**
 * Classe POJO que representa uma transação financeira
 * 
 * @author Sistema de Indexação Híbrida
 */
public class Transaction implements Comparable<Transaction> {
    private String id;
    private float valor;
    private String origem;
    private String destino;
    private String timestamp;
    
    /**
     * Construtor padrão
     */
    public Transaction() {
    }
    
    /**
     * Construtor com todos os parâmetros
     */
    public Transaction(String id, float valor, String origem, String destino, String timestamp) {
        this.id = id;
        this.valor = valor;
        this.origem = origem;
        this.destino = destino;
        this.timestamp = timestamp;
    }
    
    // Getters e Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public float getValor() {
        return valor;
    }
    
    public void setValor(float valor) {
        this.valor = valor;
    }
    
    public String getOrigem() {
        return origem;
    }
    
    public void setOrigem(String origem) {
        this.origem = origem;
    }
    
    public String getDestino() {
        return destino;
    }
    
    public void setDestino(String destino) {
        this.destino = destino;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", valor=" + valor +
                ", origem='" + origem + '\'' +
                ", destino='" + destino + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Float.compare(that.valor, valor) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(origem, that.origem) &&
                Objects.equals(destino, that.destino) &&
                Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, valor, origem, destino, timestamp);
    }
    
    /**
     * Compara transações pelo timestamp para ordenação nas árvores
     */
    @Override
    public int compareTo(Transaction other) {
        if (other == null) {
            return 1;
        }
        // Comparação por timestamp para uso nas árvores balanceadas
        return this.timestamp.compareTo(other.timestamp);
    }
}
