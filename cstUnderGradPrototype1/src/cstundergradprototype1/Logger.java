/**
 * Queue Theory Simulator
 * Prototype 1
 * @author Chris Tilstra
 * Responsible for creating and writing to the log file
 */

package cstundergradprototype1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;

//this class writes all the data to a file
public class Logger {
    
    long simStartTime = 0;
    long simEndTime = 0;
    LinkedList<String> writeQueue;
    Writer writer;
    
    //Constructor
    public Logger(String fileName){
        //create a file to write to
        makeFile(fileName);
        //initialize writeQueue (acts as a buffer)
        writeQueue = new LinkedList<>();        
    }
    
    //takes in a line and adds it to the queue to be printed to file
    public void writeLine(String line){
        writeQueue.add(line + String.format("%n"));
    }
    
    //set the start time to the current time in milliseconds
    public void startTimeNow(){
        simStartTime = System.currentTimeMillis();       
    }
    
    //set the stop time to the current time in milliseconds
    public void stopTimeNow(){
        simEndTime = System.currentTimeMillis();
    }
    
    //this method writes all the data to the file and then closes 
    //the writer
    public void stopLogger(){
        //write the start time, the list of strings to write, and end time
        System.out.println("Entering stop logger");
        try{
            writer.write("simStartTime = " + simStartTime + String.format("%n"));
            for(int i = 0; i<writeQueue.size();++i){
                writer.write(writeQueue.get(i));//TODO: this line throws an error when running a single line with a lot of customers
            }
            writer.write("simEndTime = " + simEndTime + String.format("%n"));
            writer.write("sim run time = " + (simEndTime - simStartTime) + "ms" + String.format("%n"));
        }catch (IOException ex){
            System.out.println("Logger: Error writing times to file: " + ex);
        }
        //close the file
        try{
            writer.close();
        }catch (IOException ex){
            System.out.println("Logger: Error closing writer: " + ex);
        }
        System.out.println("Exiting stop logger");
    }
    
    //this method takes a filename as an input and appends the current
    //time in millis (to ensure distinct filenames) and creates
    //the file
    private void makeFile(String fileName){
        try{
            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fileName + System.currentTimeMillis() + ".txt"), "utf-8"));
        }catch (IOException ex) {
            System.out.println("Logger: Error creating file: " + ex);
        }
    }   
}
