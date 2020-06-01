package com.company;


/**
 *
 * @author Ron Rachev
 */

public class TestGenerator extends TestBTree {
     
    
    public TestGenerator(int testNum)
    { 
        super(testNum);  
    } 
     
    public void generateTree() {
    try{
        buildTree        ();  
        removeRandomNodes();  
        System.out.println(getLogAsString());
    }catch(Exception generationException)
    {
        generationException.printStackTrace();
        System.out.println("Failed generating a random tree. stack - " + getTestNum());
    }
    }
}
