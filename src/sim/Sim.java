/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;
import java.util.*;
import static sim.Queue.INITIAL_SIZE;
/**
 *
 * @author OZLEM
 */
public class Sim {

      // Class Sim variables
    public static double Clock, MeanInterArrivalTime, MeanServiceTime, LastEventTime,
           TotalBusy, SumResponseTime;
    public static long  NumberOfCustomers, QueueLength,Length, NumberInService,
           TotalCustomers, NumberOfDepartures;

    public final static int arrival = 1;
    public final static int departure = 2;

    // Calculate the mean service and interarrival time.
    public static double TotalServiceTime = 0;
    public static double TotalInterArrivalTime = 0;

    public static EventList FutureEventList;
    public static Queue Customers;
    public static Random stream;

    public static void main(String argv[]) {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Enter average arrival time (lambda):");
        MeanInterArrivalTime=scanner.nextDouble();
        
        
        
        System.out.println("Enter average service rate:");
        MeanServiceTime=scanner.nextDouble();
        System.out.println("Enter total entities (capacity)");
        TotalCustomers=scanner.nextInt();
       
       
        
        FutureEventList = new EventList();
        Customers = new Queue();

        Initialization();

        // Loop until first "TotalCustomers" have departed
        while (NumberOfDepartures < TotalCustomers) {
            Event evt = (Event) FutureEventList.getMin();  // get imminent event
            FutureEventList.dequeue();                    // be rid of it
            Clock = evt.get_time();                       // advance simulation time
            if (evt.get_type() == arrival) {
                ProcessArrival(evt);
            } else {
                ProcessDeparture(evt);
            }
        }
        ReportGeneration();
    }

    // seed the event list with TotalCustomers arrivals
    public static void Initialization() {
        Clock = 0.0;
        QueueLength = 0;
        NumberInService = 0;
        LastEventTime = 0.0;
        TotalBusy = 0;
        SumResponseTime = 0;
        NumberOfDepartures = 0;
      

        // create first arrival event
        Event evt = new Event(arrival, exponential(MeanInterArrivalTime));
        FutureEventList.enqueue(evt);
    }

    public static void ProcessArrival(Event evt) {
        Customers.enqueue(evt);
        QueueLength++;
        // if the server is idle, fetch the event, do statistics
        // and put into service
        if (NumberInService == 0) {
            ScheduleDeparture();
        } else {
            TotalBusy += (Clock - LastEventTime);  // server is busy
        }

       

        // schedule the next arrival
        double next_interarrival_time = exponential( MeanInterArrivalTime);
        TotalInterArrivalTime += next_interarrival_time;
        Event next_arrival = new Event(arrival, Clock+next_interarrival_time);

        FutureEventList.enqueue(next_arrival);
        LastEventTime = Clock;
    }

    @SuppressWarnings("empty-statement")
    public static void ScheduleDeparture() {
        double ServiceTime;
        // get the job at the head of the queue
        while ((ServiceTime = exponential(MeanServiceTime)) < 0);
        TotalServiceTime += ServiceTime;
        Event depart = new Event(departure,Clock+ServiceTime);
        FutureEventList.enqueue( depart );
        NumberInService = 1;
        QueueLength--;
    }

    public static void ProcessDeparture(Event e) {
        // get the customer description
        Event finished = (Event) Customers.dequeue();
        // if there are customers in the queue then schedule
        // the departure of the next one
        if (QueueLength > 0) {
            ScheduleDeparture();
        } else {
            NumberInService = 0;
        }
        // measure the response time and add to the sum
        double response = (Clock - finished.get_time());
        SumResponseTime += response;
        TotalBusy += (Clock - LastEventTime );
        NumberOfDepartures++;
        LastEventTime = Clock;
    }

    public static void ReportGeneration() {
        double RHO   = TotalBusy/Clock;
        double AVGR  = SumResponseTime/TotalCustomers;
       // double p= MeanInterArrivalTime/MeanServiceTime;


        
        System.out.println( "\tMEAN INTERARRIVAL TIME                         " + (TotalInterArrivalTime / TotalCustomers) );
        System.out.println( "\tMEAN SERVICE TIME                              " + (TotalServiceTime / TotalCustomers) );
        System.out.println( "\tNUMBER OF CUSTOMERS SERVED                     " + TotalCustomers );
        System.out.println();
        System.out.println( "\tSERVER UTILIZATION                             " + RHO );
        
        System.out.println( "\tAVERAGE WAITING TIME In Queue                     " + AVGR + "  MINUTES" );
        System.out.println("\tAVERAGE WAITING TIME In System                     " + (AVGR+1/MeanServiceTime));
        
        /*
        double Lq=(p*p/(1-p));
        double Wq=(Lq/MeanInterArrivalTime);
        
        System.out.println("number in queue= Lq                                  " + (p*p/(1-p)));
        System.out.println("avg waiting time in queue                            " + (Lq/MeanInterArrivalTime));
        System.out.println("avg waiting time in system                           " + (Wq+(1/MeanServiceTime)));
        */
        System.out.println( "\tSIMULATION RUNLENGTH                           " + Clock + " MINUTES" );
        System.out.println( "\tNUMBER OF DEPARTURES                           " + TotalCustomers );
        System.out.println("\tPerc of customers who leave the sys             " + NumberOfDepartures/TotalCustomers);
    }

    public static double exponential( double mean) {
        Random rng=new Random();
        return -Math.log( rng.nextDouble() )/mean;
    }

   
    
}
