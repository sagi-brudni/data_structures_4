package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert(70);
        generatedTree.insert(13);
        generatedTree.insert(63);
        generatedTree.insert(73);
        generatedTree.insert(49);
        generatedTree.insert(11);
        generatedTree.insert(98);
        System.out.println(generatedTree);
    }
}
