package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert(35);
        generatedTree.insert(31);
        generatedTree.insert(58);
        generatedTree.insert(16);
        generatedTree.insert(61);
        generatedTree.insert(25);
        generatedTree.insert(47);
        generatedTree.insert(30);
        generatedTree.insert(54);
        generatedTree.insert(92);
        generatedTree.insert(53);
        generatedTree.insert(94);
        generatedTree.insert(20);
        generatedTree.delete(54);
        System.out.println(generatedTree);

    }
}
