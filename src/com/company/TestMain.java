package com.company;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.File; 
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths; 
import java.nio.file.StandardOpenOption;
import java.util.ArrayList; 
import java.util.Scanner;
 

/**
 *
 * @author Ron Rachev
 */


public class TestMain {
  
    /* Saves our test / info to a log file for further inspection  */
    public static void saveToLog(String fileName,ArrayList<String> logInfo)
    {
        File logFileHandle;
        try{ 
             logFileHandle = new File(fileName);
             if(!logFileHandle.exists()) 
               logFileHandle.createNewFile(); 
             Files.write(Paths.get(fileName),logInfo, StandardOpenOption.APPEND);  
        }catch(Exception logFileSaveException)
        {  // handle
        }
    }  
    
    public static ArrayList<String> parseLogFile (String filename)
    {
        try{
            ArrayList<String> logFileLines; 
            logFileLines = (ArrayList<String>) Files.readAllLines(new File("GeneratedTest.txt").toPath(), Charset.defaultCharset() );
            return logFileLines;
        }catch(Exception parseLogFileException){
            // handle 
        }
        return null;
    }
    public static void main(String [] args)
    {
      Scanner userInputScanner             = new Scanner(System.in);
      ArrayList<String> logFileGrabbedInfo = new ArrayList<>();
       
      int optionToUse = 0;
      
      System.out.println("BTree Tester            |");
      System.out.println("-------------------------"); 
       
      System.out.println("Type 1 to generate a testing file");
      System.out.println("Type 2 to run the testing file"); 
      try{
          optionToUse = userInputScanner.nextInt();
          if(optionToUse != 1 && optionToUse != 2)
          {
               System.out.println("Invalid option");
               return;
          }
      }catch(Exception parseOptionException){
          System.out.println("Invalid option");
          System.exit(0);
      }
      
      TestBTree     myTester;
      TestGenerator myTestGenerator;
      
      if(optionToUse == 1){
      int numTestsToGenerate = 0;
      
      try{
      System.out.println("Input number of tests to generate");
      numTestsToGenerate = userInputScanner.nextInt();
      
      File myTestFile = new File("GeneratedTest.txt");
      if(myTestFile.exists())
          myTestFile.createNewFile();
      
      for(int i = 0 ; i < numTestsToGenerate ; i++){
      TestGenerator myTreeGenerator = new TestGenerator(i); 
      myTreeGenerator.generateTree(); 
      saveToLog("GeneratedTest.txt",myTreeGenerator.getLog());
      System.out.println("Saved test --> " +i + " To log file!");
      }
      }catch(Exception testGenerationException){ 
          System.out.println("Error while generating a tree..");
          System.exit(0);
      }
      }else{ 
      try{
      logFileGrabbedInfo         = parseLogFile("GeneratedTest.txt");
      String entireLogFileStr    = "";
       
      for(int i = 0 ; i < logFileGrabbedInfo.size(); i++)
         entireLogFileStr += logFileGrabbedInfo.get(i)+"\r\n";
       
      String [] testSplit = entireLogFileStr.split("START - TEST ");
      
      String singleTestInfo      = ""; 
      
      ArrayList<String> passedTests = new ArrayList<>();
      ArrayList<String> failedTests = new ArrayList<>();
      
      for(int j = 1 ; j  < testSplit.length; j+=1){
          System.out.println("Running Test --> " +j);
          singleTestInfo += "START - TEST " + testSplit[j]; 
          myTester        = new TestBTree(singleTestInfo); 
          if(myTester.parseAndExecuteTest()) passedTests.add(singleTestInfo);
          else failedTests.add(singleTestInfo);
          singleTestInfo = ""; 
      }
      
      System.out.println("------\r\nDebug  |\r\n------\r\n");
      System.out.println("Success Tests - "+passedTests.size());
      System.out.println("Failed Tests - "+failedTests.size());
      System.out.println("Total Tests Ran - " + (passedTests.size()+failedTests.size()));
      System.out.println("Saving Success Tests To --> Success.txt");  
      System.out.println("Saving Failed Tests To --> Failed.txt");
      System.out.println("Check out your directory.");
      
      File successFileHandle = new File("Success.txt");
      File failedFileHandle  = new File("Failed.txt");
      
      if(successFileHandle.exists())  successFileHandle.createNewFile(); 
      if(failedFileHandle.exists())   failedFileHandle.createNewFile();
      
      saveToLog("Success.txt", passedTests); 
      saveToLog("Failed.txt", failedTests);
      }catch(Exception mainException){ 
      }
      }
    }
}
