package br.com.hybridIndexer.datastructures;

/**
 * Implementação de uma Árvore AVL (Adelson-Velsky e Landis)
 * 
 * @param <T> Tipo de dados armazenado na árvore (deve ser comparável)
 */
public class AVLTree<T extends Comparable<T>> implements BalancedTree<T> {
    private NodeAVL<T> root;
    private int size;
    
    /**
     * Construtor padrão
     */
    public AVLTree() {
        this.root = null;
        this.size = 0;
    }
    // oi
    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor não pode ser null");
        }
        root = insertRecursive(root, value);
        size++;
    }
    
    /**
     * Insere recursivamente um valor na árvore e rebalanceia
     */
    private NodeAVL<T> insertRecursive(NodeAVL<T> node, T value) {
        // Caso base: encontrou onde inserir
        if (node == null) {
            return new NodeAVL<>(value);
        }
        
        // Inserção recursiva
        int cmp = value.compareTo(node.getElement());
        if (cmp < 0) {
            node.setLeft(insertRecursive(node.getLeft(), value));
        } else if (cmp > 0) {
            node.setRight(insertRecursive(node.getRight(), value));
        } else {
            // Valor duplicado - não inserir
            size--; // Ajustar contador
            return node;
        }
        
        // Atualizar altura
        node.updateHeight();
        
        // Rebalancear se necessário
        return balance(node);
    }
    
    @Override
    public boolean remove(T value) {
        if (value == null || root == null) {
            return false;
        }
        
        int oldSize = size;
        root = removeRecursive(root, value);
        return size < oldSize;
    }
    
    /**
     * Remove recursivamente um valor da árvore e rebalanceia
     */
    private NodeAVL<T> removeRecursive(NodeAVL<T> node, T value) {
        if (node == null) {
            return null;
        }
        
        int cmp = value.compareTo(node.getElement());
        
        if (cmp < 0) {
            node.setLeft(removeRecursive(node.getLeft(), value));
        } else if (cmp > 0) {
            node.setRight(removeRecursive(node.getRight(), value));
        } else {
            // Encontrou o nó a ser removido
            size--;
            
            // Caso 1: Nó folha
            if (node.getLeft() == null && node.getRight() == null) {
                return null;
            }
            
            // Caso 2: Nó com apenas um filho
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }
            
            // Caso 3: Nó com dois filhos
            NodeAVL<T> minRight = findMin(node.getRight());
            node.setElement(minRight.getElement());
            node.setRight(removeRecursive(node.getRight(), minRight.getElement()));
        }
        
        // Atualizar altura
        node.updateHeight();
        
        // Rebalancear se necessário
        return balance(node);
    }
    
    /**
     * Encontra o menor elemento em uma subárvore
     */
    private NodeAVL<T> findMin(NodeAVL<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    
    /**
     * Balanceia um nó se necessário
     */
    private NodeAVL<T> balance(NodeAVL<T> node) {
        if (node == null) {
            return null;
        }
        
        int balanceFactor = node.getBalanceFactor();
        
        // Desbalanceado à esquerda
        if (balanceFactor > 1) {
            // Caso Esquerda-Direita
            if (node.getLeft().getBalanceFactor() < 0) {
                node.setLeft(rotateLeft(node.getLeft()));
            }
            // Caso Esquerda-Esquerda
            return rotateRight(node);
        }
        
        // Desbalanceado à direita
        if (balanceFactor < -1) {
            // Caso Direita-Esquerda
            if (node.getRight().getBalanceFactor() > 0) {
                node.setRight(rotateRight(node.getRight()));
            }
            // Caso Direita-Direita
            return rotateLeft(node);
        }
        
        return node;
    }
    
    /**
     * Rotação simples à esquerda
     */
    private NodeAVL<T> rotateLeft(NodeAVL<T> x) {
        NodeAVL<T> y = x.getRight();
        NodeAVL<T> T2 = y.getLeft();
        
        // Realizar rotação
        y.setLeft(x);
        x.setRight(T2);
        
        // Atualizar alturas
        x.updateHeight();
        y.updateHeight();
        
        return y;
    }
    
    /**
     * Rotação simples à direita
     */
    private NodeAVL<T> rotateRight(NodeAVL<T> y) {
        NodeAVL<T> x = y.getLeft();
        NodeAVL<T> T2 = x.getRight();
        
        // Realizar rotação
        x.setRight(y);
        y.setLeft(T2);
        
        // Atualizar alturas
        y.updateHeight();
        x.updateHeight();
        
        return x;
    }
    
    @Override
    public boolean find(T value) {
        if (value == null) {
            return false;
        }
        return findRecursive(root, value) != null;
    }
    
    /**
     * Busca recursiva por um valor
     */
    private NodeAVL<T> findRecursive(NodeAVL<T> node, T value) {
        if (node == null) {
            return null;
        }
        
        int cmp = value.compareTo(node.getElement());
        
        if (cmp < 0) {
            return findRecursive(node.getLeft(), value);
        } else if (cmp > 0) {
            return findRecursive(node.getRight(), value);
        } else {
            return node;
        }
    }
    
    @Override
    public int getHeight() {
        return (root == null) ? -1 : root.getHeight();
    }
    
    @Override
    public void printInOrder() {
        printInOrderRecursive(root);
        System.out.println();
    }
    
    /**
     * Percurso em ordem (in-order traversal)
     */
    private void printInOrderRecursive(NodeAVL<T> node) {
        if (node != null) {
            printInOrderRecursive(node.getLeft());
            System.out.print(node.getElement() + " ");
            printInOrderRecursive(node.getRight());
        }
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Retorna a raiz da árvore (útil para conversão)
     */
    public NodeAVL<T> getRoot() {
        return root;
    }
    
    /**
     * Percorre a árvore em ordem e aplica uma ação a cada elemento
     */
    public void inOrderTraversal(java.util.function.Consumer<T> action) {
        inOrderTraversalRecursive(root, action);
    }
    
    private void inOrderTraversalRecursive(NodeAVL<T> node, java.util.function.Consumer<T> action) {
        if (node != null) {
            inOrderTraversalRecursive(node.getLeft(), action);
            action.accept(node.getElement());
            inOrderTraversalRecursive(node.getRight(), action);
        }
    }
}
