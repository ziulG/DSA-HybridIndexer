package br.com.hybridIndexer.datastructures;

/**
 * Interface genérica para árvores balanceadas (AVL e Rubro-Negra)
 * 
 * @param <T> Tipo de dados armazenado na árvore (deve ser comparável)
 */
public interface BalancedTree<T extends Comparable<T>> {
    
    /**
     * Insere um valor na árvore
     * @param value Valor a ser inserido
     */
    void insert(T value);
    
    /**
     * Remove um valor da árvore
     * @param value Valor a ser removido
     * @return true se o valor foi removido, false caso contrário
     */
    boolean remove(T value);
    
    /**
     * Busca um valor na árvore
     * @param value Valor a ser buscado
     * @return true se o valor existe na árvore, false caso contrário
     */
    boolean find(T value);
    
    /**
     * Retorna a altura da árvore
     * @return Altura da árvore
     */
    int getHeight();
    
    /**
     * Imprime os elementos da árvore em ordem (in-order traversal)
     */
    void printInOrder();
    
    /**
     * Retorna o número de elementos na árvore
     * @return Número de elementos
     */
    int size();
    
    /**
     * Verifica se a árvore está vazia
     * @return true se a árvore está vazia, false caso contrário
     */
    boolean isEmpty();
}
