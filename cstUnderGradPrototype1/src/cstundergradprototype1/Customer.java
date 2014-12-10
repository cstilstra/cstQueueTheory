/**
 * Queue Theory Simulator
 * Prototype 1
 * @author Chris Tilstra
 * The customer object. Has a variable number of items, each
 * with a variable difficulty multiplier
 */

package cstundergradprototype1;

import java.util.LinkedList;
import java.util.Random;

public class Customer {
    
    int [] diffMultiply;//difficulty multipliers for each item the customer has
    int numItems,id,maxItems;//number of items the customer has
    long timeEntry, timeExit;//times entered and exited line
    static LinkedList<Customer>[] lines;//array of lines for customer to choose
    static Logger logger;//the logger
    static Random random;
    
    public Customer(int _id, int _maxItems){
        id = _id;
        maxItems = _maxItems;
        logger = Driver.getLogger();
        lines = Driver.getLines();
        random = new Random();
        //random number of items
        numItems = random.nextInt(maxItems)+1;//random 1-maxItems inclusive
        //initialize difficulty multiplier and set values
        diffMultiply = new int[numItems];
        for(int i=0;i<diffMultiply.length;++i){
            diffMultiply[i]=random.nextInt(20)+1;//random 1-20 inclusive
        }
    } 
    
    public int chooseLine(){
        int size=Integer.MAX_VALUE;
        int choice = 0;
        int s = random.nextInt(lines.length);//choose a random index to start at
        //loop through each line, looking for shortest
        for(int i=0;i<lines.length;++i){//loop through all lines
            if(s>=lines.length){//if we have an overflow, start back at 0
                s = 0;
            }
            if(lines[s].size()<size){//current line is shorter than choice
                size = lines[s].size();
                choice = s;//becomes new choice
            }
            s++;//increment s
        }
        return choice;
    }
    
    public int[] getDiffMult(){
        return diffMultiply;
    }
    
    public int getID(){
        return id;
    }
    
    public void setEnter(){
        timeEntry = System.currentTimeMillis();
    }
    
    public void setExit(){
        timeExit = System.currentTimeMillis();
    }
    
    public void reportTimes(){
        long totalTime = timeExit - timeEntry;
        logger.writeLine("Customer number "+id+" with "+numItems+" items, entry: "+timeEntry+" exit: "+timeExit+" total: "+totalTime);
                
    }
}
