/**
 * Queue Theory Simulator 
 * Prototype 1
 * @author Chris Tilstra
 * The driver object. Coordinates the other classes and runs the simulation
 */
package cstundergradprototype1;

import java.lang.Thread.State;
import java.util.LinkedList;

public class Driver {

    static int numCust, custDispersalRate, numLines, numCash, custHelped, maxItems,numIterations;
    static InitialQueue initialQueue;
    static LinkedList<Customer>[] lines;
    static Thread[] cashiers;
    static Logger logger;
    static Lock myLock = new Lock();
    public static boolean iqDone;
    public static boolean cashiersDone;
    static Thread iq;

    /**
     * @param args the command line arguments
     * args[0]:boolean scenario, false is 1 line for each cashier, true is 1 line for all cashiers
     * args[1]:int number of cashiers
     * args[2]:int number of customers
     * args[3]:int maximum number of items per customer
     */
    public static void main(String[] args) {
        if(args.length != 4){//incorrect number of arguments
            System.out.println("Wrong number of arguments, there should be 4 in total");
            System.out.println("the first argument is a boolean, 'false' for scenario 1, 'true' for scenario 2");
            System.out.println("all other arguments are ints in the following order:");
            System.out.println("number of cashiers, number of customers, max number of items per customer");
        }else{   
            try{
                //Populate variables:numCust,custDispersalRate,numLines,numCash,maxItems
                numCash = Integer.parseInt(args[1]);
                numCust = Integer.parseInt(args[2]);
                maxItems = Integer.parseInt(args[3]);
                if(args[0].equals("false")){
                    numLines = numCash;
                }else{
                    numLines = 1;
                }
                System.out.println("number of cashiers = "+numCash);
                System.out.println("number of lines = "+numLines);
                System.out.println("number of customers = "+numCust);
                custDispersalRate = 1000; 
            }catch(NumberFormatException e){
                System.out.println("Invalid argument: "+e);
            }
            
            logger = new Logger("testfile");//Create the logger
            logger.startTimeNow();//note the sim start time 

            logger.writeLine("Driver: number of cashiers: "+numCash);
            logger.writeLine("Driver: number of lines: "+numLines);

            custHelped = 0;

            //Build objects initialQueue,lines,cashiers
            lines = new LinkedList[numLines];
            for (int i = 0; i < lines.length; ++i) {
                lines[i] = new LinkedList();
            }
            initialQueue = new InitialQueue(numCust, custDispersalRate, maxItems);
            cashiers = new Thread[numCash];
            //populate cashiers and have them ready for work
            for (int i = 0; i < cashiers.length; ++i) {
                //TODO: randomize ability multiplier for new Cashiers
                if (lines.length < 2) {
                    cashiers[i] = new Thread(new Cashier(i, 1.0f, lines[0]));//1 line that all cashiers use
                } else {
                    cashiers[i] = new Thread(new Cashier(i, 1.0f, lines[i]));//1 line for each cashier
                }
            }
            //Run simulation        
            iq = new Thread(initialQueue);
            iq.start();
            //logger.writeLine("Driver: scenario still going");
            for (int i = 0; i < cashiers.length; i++) {//loop through all cashiers
                cashiers[i].start();//tell them to begin work
            }
            for (int i = 0; i < cashiers.length; i++) {
                if (cashiers[i].getState() != State.TERMINATED) {
                    synchronized (cashiers[i]) {
                        try {
                            System.out.printf("Waiting for cashier #%d to complete...", i);
                            cashiers[i].wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            //now that all the customers have been helped
            cashiersDone = true;
            logger.writeLine("Driver: custHelped= " + custHelped + " numCust= " + numCust);
            for (int i = 0; i < cashiers.length; ++i) {//loop through each cashier
                try {
                    cashiers[i].interrupt();//stop each cashier thread
                } catch (Exception e) {
                    System.out.println("Driver: trouble stopping cashiers :" + e);
                }
            }
            //stop the initial queue thread
            try {
                iq.interrupt();
            } catch (Exception e) {
                //ignore exception
            }
            logger.stopTimeNow();
            logger.stopLogger();
            System.out.println("Simulation Concluded.");//report finished
            
        }
    }

    //returns the logger
    public static Logger getLogger() {
        return logger;
    }

    //returns the array of lines
    public static LinkedList<Customer>[] getLines() {
        return lines;
    }

    public static void setIQDone(boolean setting) {
        iqDone = setting;
    }

    public static void incCustHelped() {
        custHelped++;
        System.out.println("Driver: custHelped= " + custHelped + " numCust= " + numCust);
        cashiersDone = (custHelped >= numCust);
    }

    //need to delete
    static int totalCustInLine() {
        int sum = 0;
        for (int i = 0; i < lines.length; ++i) {
            sum += lines[i].size();
        }
        return sum;
    }
}
