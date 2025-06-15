package br.com.hybridIndexer.datastructures;

/**
 * Classe que representa um nó de uma Árvore AVL
 * 
 * @param <T> Tipo de dados armazenado no nó (deve ser comparável)
 */
public class NodeAVL<T extends Comparable<T>> {
    private T element;
    private NodeAVL<T> left;
    private NodeAVL<T> right;
    private int height;
    
    /**
     * Construtor que cria um nó com um elemento
     * @param element Elemento a ser armazenado no nó
     */
    public NodeAVL(T element) {
        this.element = element;
        this.left = null;
        this.right = null;
        this.height = 0;
    }
    
    /**
     * Construtor completo
     * @param element Elemento a ser armazenado
     * @param left Filho esquerdo
     * @param right Filho direito
     */
    public NodeAVL(T element, NodeAVL<T> left, NodeAVL<T> right) {
        this.element = element;
        this.left = left;
        this.right = right;
        this.height = 0;
        updateHeight();
    }
    
    /**
     * Atualiza a altura do nó baseado nas alturas dos filhos
     */
    public void updateHeight() {
        int leftHeight = (left == null) ? -1 : left.height;
        int rightHeight = (right == null) ? -1 : right.height;
        this.height = Math.max(leftHeight, rightHeight) + 1;
    }
    
    /**
     * Calcula o fator de balanceamento do nó
     * @return Fator de balanceamento (altura da subárvore esquerda - altura da subárvore direita)
     */
    public int getBalanceFactor() {
        int leftHeight = (left == null) ? -1 : left.height;
        int rightHeight = (right == null) ? -1 : right.height;
        return leftHeight - rightHeight;
    }
    
    // Getters e Setters
    public T getElement() {
        return element;
    }
    
    public void setElement(T element) {
        this.element = element;
    }
    
    public NodeAVL<T> getLeft() {
        return left;
    }
    
    public void setLeft(NodeAVL<T> left) {
        this.left = left;
        updateHeight();
    }
    
    public NodeAVL<T> getRight() {
        return right;
    }
    
    public void setRight(NodeAVL<T> right) {
        this.right = right;
        updateHeight();
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    @Override
    public String toString() {
        return element.toString();
    }
}
