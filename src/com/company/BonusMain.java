package com.company;

import java.util.ArrayList;
import java.util.Scanner;


/**
 *
 * @author Ron Rachev
 */
public class BonusMain {
    
    public static void main(String [] args)
    {
        Scanner inputScanner = new Scanner(System.in); 
        System.out.println("Input number of tests :");
        int numberTests = inputScanner.nextInt();
        
        ArrayList<BonusTester> myTesters = new ArrayList<>();
        
        for(int i = 0 ; i < numberTests; i++){
        BonusTester myTester = new BonusTester(i,5);
        myTester.generateRandomTest();
        myTesters.add(myTester);
        }
        
        int passed    = 0;
        int nonPassed = 0;
        
        for(int i = 0 ; i <myTesters.size(); i++){
            if(myTesters.get(i).passedTest())
                passed++;
        }
        nonPassed = numberTests - passed;
        
        System.out.println("\r\n");
        System.out.println("Passed --> " + passed);
        System.out.println("failed --> " + nonPassed);
    }
    
}
