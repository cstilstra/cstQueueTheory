/**
 * Queue Theory Simulator
 * Prototype 1
 * @author Chris Tilstra
 * Responsible for holding all the customers, and releasing them according 
 * to the dispersal rate
 */

package cstundergradprototype1;

import java.util.LinkedList;
import java.util.Random;

public class InitialQueue implements Runnable{    
    int numCustomers,maxItems; 
    int dispersalRate,dispersalLowerBound,dispersalUpperBound;
    LinkedList<Customer> customersInitial;
    Logger logger;
    Random random;
    
    //constructor
    //takes an array of Queues (these are the lines)
    //an int for the total number of customers to go through the simulation
    //and an int for the average time between customers, in millis 
    //The array will be passed to each Customer as they are
    //released from the initialQueue
    public InitialQueue(int _numCustomers, int _dispersalRate, int _maxItems){
        maxItems = _maxItems;
        random = new Random();
        numCustomers = _numCustomers;
        dispersalRate = _dispersalRate;
        //get the range for the randomization of dispersal times
        dispersalLowerBound = (int)(dispersalRate * 0.75f);
        dispersalUpperBound = (int)(dispersalRate * 1.25f);
        logger = Driver.getLogger();
        logger.writeLine("InitialQueue: number of customers = " + numCustomers);
        logger.writeLine("InitialQueue: dispersal range = " + dispersalLowerBound + "ms - " + dispersalUpperBound + "ms");        
        customersInitial = new LinkedList<>();
        //add new customers
        for(int i=0;i<numCustomers;++i){
            customersInitial.add(new Customer(i,maxItems));
        }        
    }
    
    @Override
    public void run(){
        beginReleasing();
    }
    
    //runs in another thread to avoid interrupting the rest of the program's execution.
    public void beginReleasing(){
        for(int i=0;i<numCustomers;++i){
            //customer at head of queue chooses line
            Customer head = customersInitial.pop();
            int choice = head.chooseLine();
            //goes to line
            LinkedList<Customer>[] lines = Driver.getLines();
            lines[choice].add(head);
            System.out.printf("Customer #%d choosing line #%d.\n",head.getID(), choice);
            head.setEnter();//set the entry time for the customer
            //wait before dispersing the next customer
            waitCustom(random.nextInt(dispersalUpperBound)+dispersalLowerBound);//wait for a random amount of time between dispersal upper and lower bounds
        }
        logger.writeLine("InitialQueue: Finished releasing customers");
        Driver.setIQDone(true);//tell the driver that we've finished releasing customers
    } 
    
    private void waitCustom(long time){
        try{
                Thread.sleep(time);
            }catch(InterruptedException ex){
                    System.out.println("InitialQueue: sleep interrupted: " + ex);
            }
    }
}
