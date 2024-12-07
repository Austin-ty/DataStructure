import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Flight {
    String flightID; // Unique identifier for the flight
    public static final int maxSeats = 5; // Maximum number of seats on the flight
    ArrayList<Passenger> confirmedTicketList; // List of passengers with confirmed tickets
    Queue<Passenger> waitingList; // Queue of passengers on the waiting list
    boolean vacancyStatus; // Indicates if the flight has available seats
    private int confirmedSeats; // Number of confirmed seats
    private int emptySeats; // Number of empty seats


    public Flight(String flightID) {
        this.flightID = flightID;
        this.confirmedTicketList = new ArrayList<>();
        this.waitingList = new LinkedList<>();
        vacancyStatus = true;
        confirmedSeats = 0;
        emptySeats = maxSeats;
    }

    // Checks if the flight is full
    public boolean isFull() {
        return confirmedTicketList.size() >= maxSeats;
    }

    // Updates the vacancy status of the flight
    public void updateVacancyStatus() {
        vacancyStatus = !isFull();
    }

    // Books a flight for a passenger
    public void bookFlight(Passenger passenger, Flight flight) {
        
        // Check if the passenger has already booked or is on the waiting list
        if (confirmedTicketList.contains(passenger)) {
            System.out.println("Passenger " + passenger.getName() + " has already booked this flight.");
            return;
        }
    
        if (waitingList.contains(passenger)) {
            System.out.println("Passenger " + passenger.getName() + " is already in the waiting list.");
            return;
        }   
        
        Ticket bookingTicket = new Ticket(passenger, flight);
        passenger.addTicketToBucket(bookingTicket);

        if (!isFull()) {
            bookingTicket.setStatus("confirmed");
            addConfirmedPassenger(passenger);
            System.out.println("Ticket confirmed for passenger: " + passenger.getName());
        } else {
            bookingTicket.setStatus("waiting list");
            addWaitlistedPassenger(passenger);
            System.out.println("The flight is fully booked. Passenger " + passenger.getName() + " added to the waiting list.");
        }

        updateVacancyStatus();
        Main.afs.saveFlightsToCsv(); // Save changes to the CSV file
    }

    // Adds a passenger to the confirmed list
    public void addConfirmedPassenger(Passenger passenger) {
        confirmedTicketList.add(passenger);
        confirmedSeats++;
        emptySeats--;
    }

    // Adds a passenger to the waiting list
    public void addWaitlistedPassenger(Passenger passenger) {
        waitingList.add(passenger);
    }

    // Processes the waiting list and confirms passengers if seats are available
    public void processWaitlist() {
        while (!isFull() && !waitingList.isEmpty()) {
            Passenger nextPassenger = waitingList.poll();
            addConfirmedPassenger(nextPassenger);
        }
    }

    // Cancels a ticket for a passenger
    public boolean cancelTicket(Passenger passenger){
        if(confirmedTicketList.remove(passenger)){
            confirmedSeats--;
            emptySeats++;
            System.out.println("Ticket canceled for passenger: " + passenger.getName());
            
            // Move a passenger from the waiting list to the confirmed list if available
            if(!waitingList.isEmpty()){
                Passenger nextPassenger = waitingList.poll();
                addConfirmedPassenger(nextPassenger);
                System.out.println("Passenger " + nextPassenger.getName() + " moved from waiting list to confirmed.");
            }   
            updateVacancyStatus();
            updateTicketStatuses();
            
            return true;     
        }else{
            System.out.println("Passenger not found in the confirmed list.");
            return false;
        }
    }
        
    // Updates the statuses of tickets associated with this flight
    public void updateTicketStatuses() {
        for (Passenger p : confirmedTicketList) {
            for (Ticket t : p.getTickets()) {
                if (t.getFlight().equals(this)) {
                    t.setStatus("confirmed");
                }
            }
        }

        for (Passenger p : waitingList) {
            for (Ticket t : p.getTickets()) {
                if (t.getFlight().equals(this)) {
                    t.setStatus("waiting list");
                }
            }
        }
    }
    
    // Sets the number of confirmed seats (with validation)
    public void setConfirmedSeats(int confirmedSeats) {
        if (confirmedSeats < 0 || confirmedSeats > maxSeats) {
            System.err.println("Invalid confirmed seats value. Adjusting to default.");
            confirmedSeats = Math.max(0, Math.min(confirmedSeats, maxSeats));
        }
        this.confirmedSeats = confirmedSeats;
        this.emptySeats = maxSeats - confirmedSeats; 
    }

    // Sets the number of empty seats (with validation)
    public void setEmptySeats(int emptySeats) {
        if (emptySeats < 0 || emptySeats > maxSeats) {
            throw new IllegalArgumentException("Invalid empty seats value.");
        }
        this.emptySeats = emptySeats;
        this.confirmedSeats = maxSeats - emptySeats; 
    }


    public int getConfirmedSeats() {
        return confirmedSeats;
    }

    public int getEmptySeats() {
        return emptySeats;
    }

    public int getWaitlistCount() {
        return waitingList.size();
    }

    @Override
    public String toString() {
        return "Flight ID: " + this.flightID +
                " Total seat available: " + this.maxSeats +
                " Seat booked: " + confirmedSeats +
                " Seat available: " + emptySeats +
                " Vacancy Status: " + vacancyStatus;
    }
}