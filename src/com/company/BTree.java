package com.company;

import java.util.Arrays;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class BTree<T extends Comparable<T>> {

    // Default to 2-3 Tree
    private int minKeySize = 1;
    private int minChildrenSize = minKeySize + 1; // 2
    private int maxKeySize = 2 * minKeySize + 1; // 3
    private int maxChildrenSize = maxKeySize + 1; // 4

    private Node<T> root = null;
    private int size = 0;

    /**
     * Constructor for B-Tree which defaults to a 2-3 B-Tree.
     */
    public BTree() { }

    /**
     * Constructor for B-Tree of ordered parameter. Order here means minimum 
     * number of keys in a non-root node. 
     * 
     * @param order
     *            of the B-Tree.
     */
    public BTree(int order) {
        this.minKeySize = order - 1; // changed to be t-1
        this.minChildrenSize = minKeySize + 1;
        this.maxKeySize = 2 * order - 1; // changed to be 2t-1
        this.maxChildrenSize = maxKeySize + 1;
    }
    
    //Task 2.1
    /**
     * Insert in 1-pass to B-Tree.
     *
     * @param value
     *            The new key we want to insert.
     */
    public boolean insert(T value)
    {
        insertFromNode(root, value);
        size++; // the tree's size increased by 1
        return true;
    }

    //Task 2.1
    /**
     * delete value from B-Tree.
     *
     * @param value
     *            The key we want to remove from the tree.
     */
    public T delete(T value) {
        T removed = null;
        Node<T> node = this.getNodeAndCombine(value); // find the node that contains the given value,
                                                      // and combine every node in the way
        removed = delete(value,node);
        return removed;
    }

    /**
     * Recursive function that delete a given node from the B-tree
     *
     * @param value
     *            The key we want to remove.
     * @param node
     *            The node that contains the value to delete
     */
    private T delete(T value, Node<T> node)
    {
        if (node == null) return null;

        T removed = null;
        int index = node.indexOf(value); // the index of the value in the node between the other keys
        if(node.numberOfChildren() == 0) // if the node is leaf - CASE 1
        {
            if (node.parent != null && node.numberOfKeys() == minKeySize) // if its not the root and the node contains t-1 keys
            {
                this.combined(node);
            }
            else if (node.parent == null && node.numberOfKeys() == 1) // if its the root node with no children and only 1 key
            {
                root = null; // The Btree is null
            }
        }
        else // the node is not a leaf
        {
            Node<T> left = node.getChild(index); // getting the left child of the value in its node
            Node<T> right = node.getChild(index + 1); // getting the right child of the value in its node
            if(left.numberOfKeys() >= minKeySize + 1) // if left contains more than t-1 keys - CASE 2
            {
                // finding the predecessor, replace it with the given value and remove the predecessor
                Node<T> predecessor = this.getGreatestNode(left); // predecessor
                T replaceValue = this.delete(predecessor.keys[predecessor.numberOfKeys() - 1], predecessor); // delete predecessor
                node.addKey(replaceValue);
                size++;
            }
            else if(right.numberOfKeys() >= minKeySize + 1) // if right contains more than t-1 keys - CASE 3
            {
                // finding the successor, replace it with the given value and remove the successor
                Node<T> successor = this.getSuccessorNode(right);
                T replaceValue = this.delete(successor.keys[0], successor); // delete successor
                node.addKey(replaceValue);
                size++;
            }
            else // right and left contains exactly t-1 keys
            {
                this.merge(right); // merge with the parent
                node = right;
                removed = this.delete(value, node); // delete after merging
                return removed;
            }
        }
        size--;
        removed = node.removeKey(value); // removing the value from the node's keys
        return removed;
    }

    /**
     * returns the successor node of the given node
     *
     * @param nodeToGet
     *            The Node that we need to find it's successor
     */
    private Node<T> getSuccessorNode(Node<T> nodeToGet)
    {
        Node<T> node = nodeToGet;
        while (node.numberOfChildren() > 0)
        {
            node = node.getChild(0);
        }
        return node;
    }

    //Task 2.2
    /**
     * Insert in 2-pass to B-Tree.
     *
     * @param value
     *            The new key we want to insert.
     */
    public boolean insert2pass(T value)
    {
        Node<T> node = root;
        while (node != null) // going trough the tree, find the node where the new key should be inserted
        {
            if (node.numberOfChildren() == 0) // if the node is leaf
            {
                break; // end loop
            }

            node = navigate(node, value); // find where to insert the value
        }

        while (node != null) // ascent until we find the first node that we need to split
        {
            if (node.keysSize == maxKeySize && node.parent != null) // if the node contains 2t-1 keys, need to split
            {
                node = node.parent; // going up to the node's parent
            } else
                break; // found the node that contains less than 2t-1 keys in the path to the place we need to insert the value
        }
        insertFromNode(node, value); // insert in 1-pass from the node we found the first parent that contains less then 2t-1 keys
        size++; // the tree's size increased by 1
        return true;
    }

    /**
     * Insert in 1-pass to B-Tree from a specific node.
     *
     * @param value
     *            The new key we want to insert.
     * @param head
     *            The node we are beginning the insertion from
     */
    private boolean insertFromNode(Node<T> head, T value)
    {
        if (head == null) // if its an empty tree simply adding new node with the given value
        {
            this.root = new Node<T>(null, maxKeySize, maxChildrenSize);
            this.root.addKey(value);
        }
        else // if the root is not null, the tree is not empty
        {
            Node<T> node = head;
            while (node != null) // going trough the tree, find the node where the new key should be inserted
            {
                if (node.keysSize == maxKeySize) // if the node contains 2t-1 keys, we need to split
                    node = split(node);

                if (node.numberOfChildren() == 0) // if the node is leaf
                {
                    node.addKey(value); // adding the key to the node's keys
                    break;
                }
                node = navigate(node, value); // find where to insert the value
            }
        }
        return true;
    }

    /**
     * Navigate to the node that will contain the value, based on the node's keys and the given value
     *
     * @param value
     *            The new key we want to insert and fins its node.
     * @param node
     *            The beginning node of the search
     */
    private Node<T> navigate(Node<T> node, T value)
    {
        // Navigate

        // Lesser or equal
        T lesser = node.getKey(0);
        if (value.compareTo(lesser) <= 0)  // if the value should be the first child of the node
        {
            node = node.getChild(0);
            return node;
        }

        // Greater
        int numberOfKeys = node.numberOfKeys();
        int last = numberOfKeys - 1;
        T greater = node.getKey(last);
        if (value.compareTo(greater) > 0) // if the value should be the last child of the node
        {
            node = node.getChild(numberOfKeys);
            return node;
        }

        // Search internal nodes
        for (int i = 1; i < node.numberOfKeys(); i++) // checking all the keys in the node
        {
            T prev = node.getKey(i - 1);
            T next = node.getKey(i);
            if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                node = node.getChild(i); // getting the child in the correct place
                break;
            }
        }
        return node;
    }

    /**
     * {@inheritDoc} - The given Add function
     */
    public boolean add(T value)
    {
        if (root == null) {
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfChildren() == 0) {
                    node.addKey(value);
                    if (node.numberOfKeys() <= maxKeySize) {
                        // A-OK
                        break;
                    }                         
                    // Need to split up
                    split(node);
                    break;
                }
                // Navigate

                // Lesser or equal
                T lesser = node.getKey(0);
                if (value.compareTo(lesser) <= 0) {
                    node = node.getChild(0);
                    continue;
                }

                // Greater
                int numberOfKeys = node.numberOfKeys();
                int last = numberOfKeys - 1;
                T greater = node.getKey(last);
                if (value.compareTo(greater) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }

                // Search internal nodes
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T prev = node.getKey(i - 1);
                    T next = node.getKey(i);
                    if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                        node = node.getChild(i);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }

    /**
     * The node's key size is greater than maxKeySize, split down the middle. - The split given function
     * 
     * @param nodeToSplit
     *            to split.
     */
    private Node<T> split(Node<T> nodeToSplit) {
        Node<T> node = nodeToSplit;
        int numberOfKeys = node.numberOfKeys();
        int medianIndex = numberOfKeys / 2;
        T medianValue = node.getKey(medianIndex);

        Node<T> left = new Node<T>(null, maxKeySize, maxChildrenSize);
        for (int i = 0; i < medianIndex; i++) {
            left.addKey(node.getKey(i));
        }
        if (node.numberOfChildren() > 0) {
            for (int j = 0; j <= medianIndex; j++) {
                Node<T> c = node.getChild(j);
                left.addChild(c);
            }
        }

        Node<T> right = new Node<T>(null, maxKeySize, maxChildrenSize);
        for (int i = medianIndex + 1; i < numberOfKeys; i++) {
            right.addKey(node.getKey(i));
        }
        if (node.numberOfChildren() > 0) {
            for (int j = medianIndex + 1; j < node.numberOfChildren(); j++) {
                Node<T> c = node.getChild(j);
                right.addChild(c);
            }
        }

        if (node.parent == null) {
            // new root, height of tree is increased
            Node<T> newRoot = new Node<T>(null, maxKeySize, maxChildrenSize);
            newRoot.addKey(medianValue);
            node.parent = newRoot;
            root = newRoot;
            node = root;
            node.addChild(left);
            node.addChild(right);
            return node;
        } else {
            // Move the median value up to the parent
            Node<T> parent = node.parent;
            parent.addKey(medianValue);
            parent.removeChild(node);
            parent.addChild(left);
            parent.addChild(right);
            return parent;
        }
    }

    /**
     * {@inheritDoc} - The given remove function
     */
    public T remove(T value) {
        T removed = null;
        Node<T> node = this.getNode(value);
        removed = remove(value,node);
        return removed;
    }

    /**
     * Remove the value from the Node and check invariants - given function
     * 
     * @param value
     *            T to remove from the tree
     * @param node
     *            Node to remove value from
     * @return True if value was removed from the tree.
     */
    private T remove(T value, Node<T> node) {
        if (node == null) return null;

        T removed = null;
        int index = node.indexOf(value);
        removed = node.removeKey(value);
        if (node.numberOfChildren() == 0) {
            // leaf node
            if (node.parent != null && node.numberOfKeys() < minKeySize) {
                this.combined(node);
            } else if (node.parent == null && node.numberOfKeys() == 0) {
                // Removing root node with no keys or children
                root = null;
            }
        } else {
            // internal node
            Node<T> lesser = node.getChild(index);
            Node<T> greatest = this.getGreatestNode(lesser);
          //  T replaceValue = this.removeGreatestValue(greatest);
           // node.addKey(replaceValue);
            if (greatest.parent != null && greatest.numberOfKeys() < minKeySize) {
                this.combined(greatest);
            }
            if (greatest.numberOfChildren() > maxChildrenSize) {
                this.split(greatest);
            }
        }

        size--;

        return removed;
    }

    /**
     * Get the node with value, while taking care of the nodes that we pass by calling combine function.
     *
     * @param value
     *            to find in the tree.
     * @return Node<T> with value.
     */
    private Node<T> getNodeAndCombine(T value) {
        Node<T> node = root;
        while (node != null) {
            T lesser = node.getKey(0);
            if (value.compareTo(lesser) < 0)
            {
                if (node.numberOfChildren() > 0) // the node is not a leaf
                {
                    node = node.getChild(0);
                    if (node.numberOfKeys() == minKeySize) // if the node contains t-1 keys
                        combined(node);
                }
                else
                    node = null;
                continue;
            }

            int numberOfKeys = node.numberOfKeys();
            int last = numberOfKeys - 1;
            T greater = node.getKey(last);
            if (value.compareTo(greater) > 0) {
                if (node.numberOfChildren() > numberOfKeys)
                {
                    node = node.getChild(numberOfKeys);
                    if (node.numberOfKeys() == minKeySize) // if the node contains t-1 keys
                        combined(node);
                }
                else
                    node = null;
                continue;
            }

            for (int i = 0; i < numberOfKeys; i++) {
                T currentValue = node.getKey(i);
                if (currentValue.compareTo(value) == 0) {
                    return node;
                }

                int next = i + 1;
                if (next <= last) {
                    T nextValue = node.getKey(next);
                    if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
                        if (next < node.numberOfChildren()) {
                            node = node.getChild(next);
                            if (node.numberOfKeys() == minKeySize) // if the node contains t-1 keys
                                combined(node);
                            break;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(T value) {
        Node<T> node = getNode(value);
        return (node != null);
    }

    /**
     * Get the node with value.
     * 
     * @param value
     *            to find in the tree.
     * @return Node<T> with value.
     */
    private Node<T> getNode(T value) {
        Node<T> node = root;
        while (node != null) {
            T lesser = node.getKey(0);
            if (value.compareTo(lesser) < 0) {
                if (node.numberOfChildren() > 0)
                    node = node.getChild(0);
                else
                    node = null;
                continue;
            }

            int numberOfKeys = node.numberOfKeys();
            int last = numberOfKeys - 1;
            T greater = node.getKey(last);
            if (value.compareTo(greater) > 0) {
                if (node.numberOfChildren() > numberOfKeys)
                    node = node.getChild(numberOfKeys);
                else
                    node = null;
                continue;
            }

            for (int i = 0; i < numberOfKeys; i++) {
                T currentValue = node.getKey(i);
                if (currentValue.compareTo(value) == 0) {
                    return node;
                }

                int next = i + 1;
                if (next <= last) {
                    T nextValue = node.getKey(next);
                    if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
                        if (next < node.numberOfChildren()) {
                            node = node.getChild(next);
                            break;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the greatest valued child from node.
     * 
     * @param nodeToGet
     *            child with the greatest value.
     * @return Node<T> child with greatest value.
     */
    private Node<T> getGreatestNode(Node<T> nodeToGet) {
        Node<T> node = nodeToGet;
        while (node.numberOfChildren() > 0) {
            node = node.getChild(node.numberOfChildren() - 1);
        }
        return node;
    }

    /**
     * Merge 2 children with their parent.
     *
     * @param node
     *           one of the parent's child.
     */
    private void merge(Node<T> node)
    {
        Node<T> parent = node.parent;
        int index = parent.indexOf(node);
        int indexOfLeftNeighbor = index - 1;
        int indexOfRightNeighbor = index + 1;

        Node<T> rightNeighbor = null;
        int rightNeighborSize = -minChildrenSize;
        if (indexOfRightNeighbor < parent.numberOfChildren()) // if right neighbor exists
        {
            rightNeighbor = parent.getChild(indexOfRightNeighbor); // getting the child in the matching index from the parent
            rightNeighborSize = rightNeighbor.numberOfKeys();
        }

        Node<T> leftNeighbor = null;
        int leftNeighborSize = -minChildrenSize;
        if (indexOfLeftNeighbor >= 0) // if left neighbor exists
        {
            leftNeighbor = parent.getChild(indexOfLeftNeighbor); // getting the child in the matching index from the parent
            leftNeighborSize = leftNeighbor.numberOfKeys();
        }

        if (leftNeighbor != null && parent.numberOfKeys() > 0) // trying to merge with left neighbor
        {
            T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
            int prev = getIndexOfNextValue(parent, removeValue);
            T parentValue = parent.removeKey(prev);
            parent.removeChild(leftNeighbor);
            node.addKey(parentValue);
            for (int i = 0; i < leftNeighbor.keysSize; i++) // getting all the left neighbor's keys
            {
                T v = leftNeighbor.getKey(i);
                node.addKey(v);
            }
            for (int i = 0; i < leftNeighbor.childrenSize; i++)
            {
                Node<T> c = leftNeighbor.getChild(i); // getting all the left neighbor's children
                node.addChild(c);
            }

            if (parent.parent != null && parent.numberOfKeys() < minKeySize)
                this.combined(parent);// removing key made parent too small, combined up tree
            else if (parent.numberOfKeys() == 0) // parent no longer has keys, make this node the new root
            {
                node.parent = null;
                root = node;
            }
        }
        else if (rightNeighbor != null && parent.numberOfKeys() > 0) // trying to merge with right neighbor
        {
            // Can't borrow from neighbors, try to combined with right neighbor
            T removeValue = rightNeighbor.getKey(0);
            int prev = getIndexOfPreviousValue(parent, removeValue);
            T parentValue = parent.removeKey(prev);
            parent.removeChild(rightNeighbor);
            node.addKey(parentValue);
            for (int i = 0; i < rightNeighbor.keysSize; i++) // getting all the right neighbor's keys
            {
                T v = rightNeighbor.getKey(i);
                node.addKey(v);
            }
            for (int i = 0; i < rightNeighbor.childrenSize; i++) // getting all the right neighbor's children
            {
                Node<T> c = rightNeighbor.getChild(i);
                node.addChild(c);
            }

            if (parent.parent != null && parent.numberOfKeys() < minKeySize)
                this.combined(parent); // removing key made parent too small, combined up tree
             else if (parent.numberOfKeys() == 0)
             {
                node.parent = null; // parent no longer has keys, make this node the new root
                root = node;
            }
        }
    }
    /**
     * Combined children keys with parent when size is less than minKeySize.
     * 
     * @param node
     *            with children to combined.
     * @return True if combined successfully.
     */
    private boolean combined(Node<T> node)
    {
        Node<T> parent = node.parent;
        int index = parent.indexOf(node);
        int indexOfLeftNeighbor = index - 1;
        int indexOfRightNeighbor = index + 1;
        Node<T> leftNeighbor = null;
        int leftNeighborSize = -minChildrenSize;
        if (indexOfLeftNeighbor >= 0) {
            leftNeighbor = parent.getChild(indexOfLeftNeighbor);
            leftNeighborSize = leftNeighbor.numberOfKeys();
        }

        //shift from left neighbor
        if (leftNeighbor != null && leftNeighborSize > minKeySize)
        {
            // Try to borrow from left neighbor
            T removeValue = leftNeighbor.getKey(leftNeighbor.numberOfKeys() - 1);
            int prev = getIndexOfNextValue(parent, removeValue);
            T parentValue = parent.removeKey(prev);
            T neighborValue = leftNeighbor.removeKey(leftNeighbor.numberOfKeys() - 1);
            node.addKey(parentValue);
            parent.addKey(neighborValue);
            if (leftNeighbor.numberOfChildren() > 0) {
                node.addChild(leftNeighbor.removeChild(leftNeighbor.numberOfChildren() - 1));
            }
        }
        else { // shift from right neighbor
            Node<T> rightNeighbor = null;
            int rightNeighborSize = -minChildrenSize;
            if (indexOfRightNeighbor < parent.numberOfChildren()) {
                rightNeighbor = parent.getChild(indexOfRightNeighbor);
                rightNeighborSize = rightNeighbor.numberOfKeys();
            }

            // Try to borrow neighbor from right
            if (rightNeighbor != null && rightNeighborSize > minKeySize) {
                // Try to borrow from right neighbor
                T removeValue = rightNeighbor.getKey(0);
                int prev = getIndexOfPreviousValue(parent, removeValue);
                T parentValue = parent.removeKey(prev);
                T neighborValue = rightNeighbor.removeKey(0);
                node.addKey(parentValue);
                parent.addKey(neighborValue);
                if (rightNeighbor.numberOfChildren() > 0) {
                    node.addChild(rightNeighbor.removeChild(0));
                }
            } else // no neighbors to borrow - can't shift from any neighbor
                merge(node);
        }
        return true;
    }

    /**
     * Get the index of previous key in node.
     * 
     * @param node
     *            to find the previous key in.
     * @param value
     *            to find a previous value for.
     * @return index of previous key or -1 if not found.
     */
    private int getIndexOfPreviousValue(Node<T> node, T value) {
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i - 1;
        }
        return node.numberOfKeys() - 1;
    }

    /**
     * Get the index of next key in node.
     * 
     * @param node
     *            to find the next key in.
     * @param value
     *            to find a next value for.
     * @return index of next key or -1 if not found.
     */
    private int getIndexOfNextValue(Node<T> node, T value) {
        for (int i = 0; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i;
        }
        return node.numberOfKeys() - 1;
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validate() {
        if (root == null) return true;
        return validateNode(root);
    }

    /**
     * Validate the node according to the B-Tree invariants.
     * 
     * @param node
     *            to validate.
     * @return True if valid.
     */
    private boolean validateNode(Node<T> node) {
        int keySize = node.numberOfKeys();
        if (keySize > 1) {
            // Make sure the keys are sorted
            for (int i = 1; i < keySize; i++) {
                T p = node.getKey(i - 1);
                T n = node.getKey(i);
                if (p.compareTo(n) > 0)
                    return false;
            }
        }
        int childrenSize = node.numberOfChildren();
        if (node.parent == null) {
            // root
            if (keySize > maxKeySize) {
                // check max key size. root does not have a min key size
                return false;
            } else if (childrenSize == 0) {
                // if root, no children, and keys are valid
                return true;
            } else if (childrenSize < 2) {
                // root should have zero or at least two children
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        } else {
            // non-root
            if (keySize < minKeySize) {
                return false;
            } else if (keySize > maxKeySize) {
                return false;
            } else if (childrenSize == 0) {
                return true;
            } else if (keySize != (childrenSize - 1)) {
                // If there are chilren, there should be one more child then
                // keys
                return false;
            } else if (childrenSize < minChildrenSize) {
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        }

        Node<T> first = node.getChild(0);
        // The first child's last key should be less than the node's first key
        if (first.getKey(first.numberOfKeys() - 1).compareTo(node.getKey(0)) > 0)
            return false;

        Node<T> last = node.getChild(node.numberOfChildren() - 1);
        // The last child's first key should be greater than the node's last key
        if (last.getKey(0).compareTo(node.getKey(node.numberOfKeys() - 1)) < 0)
            return false;

        // Check that each node's first and last key holds it's invariance
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T p = node.getKey(i - 1);
            T n = node.getKey(i);
            Node<T> c = node.getChild(i);
            if (p.compareTo(c.getKey(0)) > 0)
                return false;
            if (n.compareTo(c.getKey(c.numberOfKeys() - 1)) < 0)
                return false;
        }

        for (int i = 0; i < node.childrenSize; i++) {
            Node<T> c = node.getChild(i);
            boolean valid = this.validateNode(c);
            if (!valid)
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }
    
    
    private static class Node<T extends Comparable<T>> {

        private T[] keys = null;
        private int keysSize = 0;
        private Node<T>[] children = null;
        private int childrenSize = 0;
        private Comparator<Node<T>> comparator = new Comparator<Node<T>>() {
            public int compare(Node<T> arg0, Node<T> arg1) {
                return arg0.getKey(0).compareTo(arg1.getKey(0));
            }
        };

        protected Node<T> parent = null;

        private Node(Node<T> parent, int maxKeySize, int maxChildrenSize) {
            this.parent = parent;
            this.keys = (T[]) new Comparable[maxKeySize + 1];
            this.keysSize = 0;
            this.children = new Node[maxChildrenSize + 1];
            this.childrenSize = 0;
        }

        private T getKey(int index) {
            return keys[index];
        }

        private int indexOf(T value) {
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) return i;
            }
            return -1;
        }

        private void addKey(T value) {
            keys[keysSize++] = value;
            Arrays.sort(keys, 0, keysSize);
        }

        private T removeKey(T value) {
            T removed = null;
            boolean found = false;
            if (keysSize == 0) return null;
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) {
                    found = true;
                    removed = keys[i];
                } else if (found) {
                    // shift the rest of the keys down
                    keys[i - 1] = keys[i];
                }
            }
            if (found) {
                keysSize--;
                keys[keysSize] = null;
            }
            return removed;
        }

        private T removeKey(int index) {
            if (index >= keysSize)
                return null;
            T value = keys[index];
            for (int i = index + 1; i < keysSize; i++) {
                // shift the rest of the keys down
                keys[i - 1] = keys[i];
            }
            keysSize--;
            keys[keysSize] = null;
            return value;
        }

        private int numberOfKeys() {
            return keysSize;
        }

        private Node<T> getChild(int index) {
            if (index >= childrenSize)
                return null;
            return children[index];
        }

        private int indexOf(Node<T> child) {
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child))
                    return i;
            }
            return -1;
        }

        private boolean addChild(Node<T> child) {
            child.parent = this;
            children[childrenSize++] = child;
            Arrays.sort(children, 0, childrenSize, comparator);
            return true;
        }

        private boolean removeChild(Node<T> child) {
            boolean found = false;
            if (childrenSize == 0)
                return found;
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child)) {
                    found = true;
                } else if (found) {
                    // shift the rest of the keys down
                    children[i - 1] = children[i];
                }
            }
            if (found) {
                childrenSize--;
                children[childrenSize] = null;
            }
            return found;
        }

        private Node<T> removeChild(int index) {
            if (index >= childrenSize)
                return null;
            Node<T> value = children[index];
            children[index] = null;
            for (int i = index + 1; i < childrenSize; i++) {
                // shift the rest of the keys down
                children[i - 1] = children[i];
            }
            childrenSize--;
            children[childrenSize] = null;
            return value;
        }

        private int numberOfChildren() {
            return childrenSize;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("keys=[");
            for (int i = 0; i < numberOfKeys(); i++) {
                T value = getKey(i);
                builder.append(value);
                if (i < numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("]\n");

            if (parent != null) {
                builder.append("parent=[");
                for (int i = 0; i < parent.numberOfKeys(); i++) {
                    T value = parent.getKey(i);
                    builder.append(value);
                    if (i < parent.numberOfKeys() - 1)
                        builder.append(", ");
                }
                builder.append("]\n");
            }

            if (children != null) {
                builder.append("keySize=").append(numberOfKeys()).append(" children=").append(numberOfChildren()).append("\n");
            }

            return builder.toString();
        }
    }

    private static class TreePrinter {

        public static <T extends Comparable<T>> String getString(BTree<T> tree) {
            if (tree.root == null) return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static <T extends Comparable<T>> String getString(Node<T> node, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            builder.append(prefix).append((isTail ? "└── " : "├── "));
            for (int i = 0; i < node.numberOfKeys(); i++) {
                T value = node.getKey(i);
                builder.append(value);
                if (i < node.numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("\n");

            if (node.children != null) {
                for (int i = 0; i < node.numberOfChildren() - 1; i++) {
                    Node<T> obj = node.getChild(i);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), false));
                }
                if (node.numberOfChildren() >= 1) {
                    Node<T> obj = node.getChild(node.numberOfChildren() - 1);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), true));
                }
            }

            return builder.toString();
        }
    }

}