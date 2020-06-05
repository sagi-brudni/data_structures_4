package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert(55);
        generatedTree.insert(94);
        generatedTree.insert(23);
        generatedTree.insert(75);
        generatedTree.insert(30);
        generatedTree.insert(57);
        generatedTree.insert(47);
        generatedTree.insert(21);
        generatedTree.insert(36);
        generatedTree.insert(91);
        generatedTree.insert(48);
        generatedTree.insert(19);

        generatedTree.delete(55);
        generatedTree.delete(48);
        generatedTree.delete(30);

        System.out.println(generatedTree);
    }
}
