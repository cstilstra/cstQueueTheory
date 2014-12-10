/**
 * Queue Theory Simulator 
 * Prototype 1
 * @author Chris Tilstra 
 * The cashier object. Is associated with a line, and
 * processes all customers from that line
 */
package cstundergradprototype1;

import java.util.LinkedList;

public class Cashier implements Runnable {

    float abilityMult;
    LinkedList<Customer> line;
    boolean workIsDone;
    int[] custDiff;
    int id, custsHelped;

    //constructor
    public Cashier(int id, float _abilityMult, LinkedList<Customer> _line) {
        abilityMult = _abilityMult;
        line = _line;
        workIsDone = false;
        this.id = id;
        custsHelped = 0;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (!Driver.iqDone || line.size() > 0) {//work until the Driver says to stop
                try {
                    Driver.myLock.lock();
                    if (line.size() > 0) {//if there is a customer at the head of the line
                      
                        Customer currentCust = line.pop();//grab the customer
                        Driver.myLock.unlock();
                        currentCust.setExit();//customer notes time exited line
                        custDiff = currentCust.getDiffMult();//get the customer's difficulty multipliers
                        for (int i = 0; i < custDiff.length; ++i) {//loop through customer's diff multipliers
                            custWorkTime((long) (abilityMult * custDiff[i]) * 50);//do work for the current item
                        }
                        currentCust.reportTimes();//the customer writes to the logger
                        Driver.incCustHelped();//tell the driver we've helped another customer
                        custsHelped++;//increment personal count of customers helped
                        System.out.printf("Cashier #%d: I just finished helping a customer.\n", id);
                    } else {//if there is no customer at the head of the line
                        Driver.myLock.unlock();
                        custWorkTime(500);//wait before checking for another customer
                    }
                } catch (Exception e) {
                   System.out.printf("Exception here!!! %s\n", e);
                }
            }//while
            Driver.getLogger().writeLine("Cashier #"+id+": Helped "+custsHelped+" customers");//print number of customers helped to logfile
            notify();
        }
    }

    void custWorkTime(long timeToWork) {
        try {
            Thread.sleep(timeToWork);
        } catch (InterruptedException ex) {
            System.out.println("Cashier: work interrupted: " + ex);
        }
    }

    public void finishWork() {
        workIsDone = true;
    }
}
