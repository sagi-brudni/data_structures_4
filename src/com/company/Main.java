package com.company;

public class Main {

    public static void main(String[] args)
    {
        BTree<Integer> generatedTree = new BTree<Integer>(2);
        generatedTree.insert2pass(83);
        generatedTree.insert2pass(30);
        generatedTree.insert2pass(32);
        generatedTree.insert2pass(48);
        generatedTree.insert2pass(18);
        generatedTree.insert2pass(31);
        generatedTree.insert2pass(37);
        generatedTree.insert2pass(40);
        generatedTree.insert2pass(67);
        generatedTree.insert2pass(63);
        generatedTree.insert2pass(11);
        generatedTree.insert2pass(38);
        generatedTree.insert2pass(96);
        generatedTree.insert2pass(34);
        generatedTree.insert2pass(22);


        generatedTree.delete(32);
        generatedTree.delete(40);
        System.out.println(generatedTree);


    }
}
