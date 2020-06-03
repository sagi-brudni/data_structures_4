package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert(41);
        generatedTree.insert(28);
        generatedTree.insert(89);
        generatedTree.insert(82);
        generatedTree.insert(97);
        generatedTree.insert(69);
        generatedTree.insert(32);
        generatedTree.insert(61);
        generatedTree.insert(64);
        generatedTree.delete(61);
        generatedTree.delete(89);
        System.out.println(generatedTree);
        generatedTree.delete(82);
    }
}
