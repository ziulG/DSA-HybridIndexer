package br.com.hybridIndexer.datastructures;

/**
 * Classe que representa um nó de uma Árvore Rubro-Negra
 * 
 * @param <T> Tipo de dados armazenado no nó (deve ser comparável)
 */
public class NodeRB<T extends Comparable<T>> {
    // Enumeração para as cores dos nós
    public enum Color {
        RED, BLACK
    }
    
    private T element;
    private NodeRB<T> parent;
    private NodeRB<T> left;
    private NodeRB<T> right;
    private Color color;
    
    /**
     * Construtor que cria um nó vermelho com um elemento
     * @param element Elemento a ser armazenado no nó
     */
    public NodeRB(T element) {
        this.element = element;
        this.parent = null;
        this.left = null;
        this.right = null;
        this.color = Color.RED; // Novos nós sempre começam vermelhos
    }
    
    /**
     * Construtor completo
     * @param element Elemento a ser armazenado
     * @param color Cor do nó
     */
    public NodeRB(T element, Color color) {
        this.element = element;
        this.parent = null;
        this.left = null;
        this.right = null;
        this.color = color;
    }
    
    /**
     * Verifica se o nó é vermelho
     * @return true se o nó é vermelho, false se é preto
     */
    public boolean isRed() {
        return color == Color.RED;
    }
    
    /**
     * Verifica se o nó é preto
     * @return true se o nó é preto, false se é vermelho
     */
    public boolean isBlack() {
        return color == Color.BLACK;
    }
    
    /**
     * Retorna o tio do nó (irmão do pai)
     * @return O nó tio ou null se não existir
     */
    public NodeRB<T> getUncle() {
        if (parent == null || parent.parent == null) {
            return null;
        }
        
        NodeRB<T> grandparent = parent.parent;
        if (parent == grandparent.left) {
            return grandparent.right;
        } else {
            return grandparent.left;
        }
    }
    
    /**
     * Retorna o irmão do nó
     * @return O nó irmão ou null se não existir
     */
    public NodeRB<T> getSibling() {
        if (parent == null) {
            return null;
        }
        
        if (this == parent.left) {
            return parent.right;
        } else {
            return parent.left;
        }
    }
    
    /**
     * Verifica se o nó é filho esquerdo
     * @return true se é filho esquerdo, false caso contrário
     */
    public boolean isLeftChild() {
        return parent != null && this == parent.left;
    }
    
    /**
     * Verifica se o nó é filho direito
     * @return true se é filho direito, false caso contrário
     */
    public boolean isRightChild() {
        return parent != null && this == parent.right;
    }
    
    // Getters e Setters
    public T getElement() {
        return element;
    }
    
    public void setElement(T element) {
        this.element = element;
    }
    
    public NodeRB<T> getParent() {
        return parent;
    }
    
    public void setParent(NodeRB<T> parent) {
        this.parent = parent;
    }
    
    public NodeRB<T> getLeft() {
        return left;
    }
    
    public void setLeft(NodeRB<T> left) {
        this.left = left;
        if (left != null) {
            left.parent = this;
        }
    }
    
    public NodeRB<T> getRight() {
        return right;
    }
    
    public void setRight(NodeRB<T> right) {
        this.right = right;
        if (right != null) {
            right.parent = this;
        }
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return element.toString() + "(" + (isRed() ? "R" : "B") + ")";
    }
}
