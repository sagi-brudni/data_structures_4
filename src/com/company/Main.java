package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert(56);
        generatedTree.insert(46);
        generatedTree.insert(64);
        generatedTree.insert(86);
        generatedTree.insert(88);
        generatedTree.insert(94);
        generatedTree.insert(13);
        generatedTree.insert(32);
        generatedTree.insert(78);
        generatedTree.insert(69);
        generatedTree.insert(92);
        generatedTree.insert(96);
        generatedTree.insert(95);
        generatedTree.insert(28);

        generatedTree.delete(88);
        generatedTree.delete(86);

        System.out.println(generatedTree);
    }
}
