package br.com.hybridIndexer.datastructures;

/**
 * Implementação de uma Árvore Rubro-Negra (Red-Black Tree)
 * 
 * @param <T> Tipo de dados armazenado na árvore (deve ser comparável)
 */
public class RedBlackTree<T extends Comparable<T>> implements BalancedTree<T> {
    private NodeRB<T> root;
    private NodeRB<T> TNULL; // Nó sentinela para representar NULL
    private int size;
    
    /**
     * Construtor padrão
     */
    public RedBlackTree() {
        TNULL = new NodeRB<>(null);
        TNULL.setColor(NodeRB.Color.BLACK);
        root = TNULL;
        size = 0;
    }
    
    @Override
    public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor não pode ser null");
        }
        
        NodeRB<T> newNode = new NodeRB<>(value);
        newNode.setLeft(TNULL);
        newNode.setRight(TNULL);
        
        NodeRB<T> y = null;
        NodeRB<T> x = this.root;
        
        // Encontrar onde inserir o novo nó
        while (x != TNULL) {
            y = x;
            int cmp = newNode.getElement().compareTo(x.getElement());
            if (cmp < 0) {
                x = x.getLeft();
            } else if (cmp > 0) {
                x = x.getRight();
            } else {
                // Valor duplicado - não inserir
                return;
            }
        }
        
        // y é o pai do novo nó
        newNode.setParent(y);
        if (y == null) {
            root = newNode;
        } else if (newNode.getElement().compareTo(y.getElement()) < 0) {
            y.setLeft(newNode);
        } else {
            y.setRight(newNode);
        }
        
        // Se o novo nó é a raiz, pintar de preto e retornar
        if (newNode.getParent() == null) {
            newNode.setColor(NodeRB.Color.BLACK);
            size++;
            return;
        }
        
        // Se o avô é null, retornar
        if (newNode.getParent().getParent() == null) {
            size++;
            return;
        }
        
        size++;
        
        // Corrigir a árvore
        fixInsert(newNode);
    }
    
    /**
     * Corrige violações após inserção
     */
    private void fixInsert(NodeRB<T> k) {
        NodeRB<T> u;
        while (k.getParent() != null && k.getParent().isRed()) {
            if (k.getParent() == k.getParent().getParent().getRight()) {
                u = k.getParent().getParent().getLeft(); // tio
                if (u != null && u != TNULL && u.isRed()) {
                    // Caso 1: tio é vermelho
                    u.setColor(NodeRB.Color.BLACK);
                    k.getParent().setColor(NodeRB.Color.BLACK);
                    k.getParent().getParent().setColor(NodeRB.Color.RED);
                    k = k.getParent().getParent();
                } else {
                    // Caso 2: tio é preto
                    if (k == k.getParent().getLeft()) {
                        // Caso 2.1: k é filho esquerdo
                        k = k.getParent();
                        rightRotate(k);
                    }
                    // Caso 2.2: k é filho direito
                    k.getParent().setColor(NodeRB.Color.BLACK);
                    k.getParent().getParent().setColor(NodeRB.Color.RED);
                    leftRotate(k.getParent().getParent());
                }
            } else {
                u = k.getParent().getParent().getRight(); // tio
                
                if (u != null && u != TNULL && u.isRed()) {
                    // Caso 1: tio é vermelho
                    u.setColor(NodeRB.Color.BLACK);
                    k.getParent().setColor(NodeRB.Color.BLACK);
                    k.getParent().getParent().setColor(NodeRB.Color.RED);
                    k = k.getParent().getParent();
                } else {
                    // Caso 2: tio é preto
                    if (k == k.getParent().getRight()) {
                        // Caso 2.1: k é filho direito
                        k = k.getParent();
                        leftRotate(k);
                    }
                    // Caso 2.2: k é filho esquerdo
                    k.getParent().setColor(NodeRB.Color.BLACK);
                    k.getParent().getParent().setColor(NodeRB.Color.RED);
                    rightRotate(k.getParent().getParent());
                }
            }
            if (k == root) {
                break;
            }
        }
        root.setColor(NodeRB.Color.BLACK);
    }
    
    /**
     * Rotação à esquerda
     */
    private void leftRotate(NodeRB<T> x) {
        NodeRB<T> y = x.getRight();
        x.setRight(y.getLeft());
        if (y.getLeft() != TNULL) {
            y.getLeft().setParent(x);
        }
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getLeft()) {
            x.getParent().setLeft(y);
        } else {
            x.getParent().setRight(y);
        }
        y.setLeft(x);
        x.setParent(y);
    }
    
    /**
     * Rotação à direita
     */
    private void rightRotate(NodeRB<T> x) {
        NodeRB<T> y = x.getLeft();
        x.setLeft(y.getRight());
        if (y.getRight() != TNULL) {
            y.getRight().setParent(x);
        }
        y.setParent(x.getParent());
        if (x.getParent() == null) {
            this.root = y;
        } else if (x == x.getParent().getRight()) {
            x.getParent().setRight(y);
        } else {
            x.getParent().setLeft(y);
        }
        y.setRight(x);
        x.setParent(y);
    }
    
    @Override
    public boolean remove(T value) {
        NodeRB<T> node = searchNode(root, value);
        if (node == TNULL || node == null) {
            return false;
        }
        deleteNode(node);
        size--;
        return true;
    }
    
    /**
     * Remove um nó da árvore
     */
    private void deleteNode(NodeRB<T> node) {
        NodeRB<T> x, y;
        y = node;
        NodeRB.Color yOriginalColor = y.getColor();
        
        if (node.getLeft() == TNULL) {
            x = node.getRight();
            transplant(node, node.getRight());
        } else if (node.getRight() == TNULL) {
            x = node.getLeft();
            transplant(node, node.getLeft());
        } else {
            y = minimum(node.getRight());
            yOriginalColor = y.getColor();
            x = y.getRight();
            if (y.getParent() == node) {
                if (x != null) x.setParent(y);
            } else {
                transplant(y, y.getRight());
                y.setRight(node.getRight());
                y.getRight().setParent(y);
            }
            transplant(node, y);
            y.setLeft(node.getLeft());
            y.getLeft().setParent(y);
            y.setColor(node.getColor());
        }
        
        if (yOriginalColor == NodeRB.Color.BLACK) {
            fixDelete(x);
        }
    }
    
    /**
     * Corrige violações após remoção
     */
    private void fixDelete(NodeRB<T> x) {
        NodeRB<T> s;
        while (x != root && x != null && x.isBlack()) {
            if (x == x.getParent().getLeft()) {
                s = x.getParent().getRight();
                if (s.isRed()) {
                    s.setColor(NodeRB.Color.BLACK);
                    x.getParent().setColor(NodeRB.Color.RED);
                    leftRotate(x.getParent());
                    s = x.getParent().getRight();
                }
                
                if ((s.getLeft() == null || s.getLeft().isBlack()) && 
                    (s.getRight() == null || s.getRight().isBlack())) {
                    s.setColor(NodeRB.Color.RED);
                    x = x.getParent();
                } else {
                    if (s.getRight() == null || s.getRight().isBlack()) {
                        if (s.getLeft() != null) s.getLeft().setColor(NodeRB.Color.BLACK);
                        s.setColor(NodeRB.Color.RED);
                        rightRotate(s);
                        s = x.getParent().getRight();
                    }
                    
                    s.setColor(x.getParent().getColor());
                    x.getParent().setColor(NodeRB.Color.BLACK);
                    if (s.getRight() != null) s.getRight().setColor(NodeRB.Color.BLACK);
                    leftRotate(x.getParent());
                    x = root;
                }
            } else {
                s = x.getParent().getLeft();
                if (s.isRed()) {
                    s.setColor(NodeRB.Color.BLACK);
                    x.getParent().setColor(NodeRB.Color.RED);
                    rightRotate(x.getParent());
                    s = x.getParent().getLeft();
                }
                
                if ((s.getRight() == null || s.getRight().isBlack()) && 
                    (s.getLeft() == null || s.getLeft().isBlack())) {
                    s.setColor(NodeRB.Color.RED);
                    x = x.getParent();
                } else {
                    if (s.getLeft() == null || s.getLeft().isBlack()) {
                        if (s.getRight() != null) s.getRight().setColor(NodeRB.Color.BLACK);
                        s.setColor(NodeRB.Color.RED);
                        leftRotate(s);
                        s = x.getParent().getLeft();
                    }
                    
                    s.setColor(x.getParent().getColor());
                    x.getParent().setColor(NodeRB.Color.BLACK);
                    if (s.getLeft() != null) s.getLeft().setColor(NodeRB.Color.BLACK);
                    rightRotate(x.getParent());
                    x = root;
                }
            }
        }
        if (x != null) x.setColor(NodeRB.Color.BLACK);
    }
    
    /**
     * Substitui uma subárvore por outra
     */
    private void transplant(NodeRB<T> u, NodeRB<T> v) {
        if (u.getParent() == null) {
            root = v;
        } else if (u == u.getParent().getLeft()) {
            u.getParent().setLeft(v);
        } else {
            u.getParent().setRight(v);
        }
        if (v != null && v != TNULL) {
            v.setParent(u.getParent());
        }
    }
    
    /**
     * Encontra o nó com o menor valor em uma subárvore
     */
    private NodeRB<T> minimum(NodeRB<T> node) {
        while (node.getLeft() != TNULL && node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }
    
    @Override
    public boolean find(T value) {
        return searchNode(root, value) != TNULL;
    }
    
    /**
     * Busca um nó com um valor específico
     */
    private NodeRB<T> searchNode(NodeRB<T> node, T value) {
        if (node == TNULL || node == null || value == null) {
            return TNULL;
        }
        
        int cmp = value.compareTo(node.getElement());
        
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchNode(node.getLeft(), value);
        } else {
            return searchNode(node.getRight(), value);
        }
    }
    
    @Override
    public int getHeight() {
        return getHeightRecursive(root);
    }
    
    /**
     * Calcula a altura da árvore recursivamente
     */
    private int getHeightRecursive(NodeRB<T> node) {
        if (node == null || node == TNULL) {
            return -1;
        }
        
        int leftHeight = getHeightRecursive(node.getLeft());
        int rightHeight = getHeightRecursive(node.getRight());
        
        return Math.max(leftHeight, rightHeight) + 1;
    }
    
    @Override
    public void printInOrder() {
        printInOrderRecursive(root);
        System.out.println();
    }
    
    /**
     * Percurso em ordem (in-order traversal)
     */
    private void printInOrderRecursive(NodeRB<T> node) {
        if (node != TNULL && node != null) {
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
    public NodeRB<T> getRoot() {
        return root;
    }
    
    /**
     * Percorre a árvore em ordem e aplica uma ação a cada elemento
     */
    public void inOrderTraversal(java.util.function.Consumer<T> action) {
        inOrderTraversalRecursive(root, action);
    }
    
    private void inOrderTraversalRecursive(NodeRB<T> node, java.util.function.Consumer<T> action) {
        if (node != TNULL && node != null) {
            inOrderTraversalRecursive(node.getLeft(), action);
            action.accept(node.getElement());
            inOrderTraversalRecursive(node.getRight(), action);
        }
    }
}
