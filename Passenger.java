import java.util.ArrayList;
import java.util.List;

public class Passenger {

    private String passportNum; // Passenger's passport number
    private String name; // Passenger's name
    private ArrayList<Ticket> ticketsBucket; // List to store the passenger's tickets

    public Passenger(String passportNum, String name){
        this.passportNum = passportNum;
        this.name = name;
        ticketsBucket = new ArrayList<>();
    }

    // Getters and setters for name and passport number
    public String getName() {
        return name;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    // Adds a ticket to the passenger's ticket bucket
    public void addTicketToBucket(Ticket ticket) {
        // Check if the passenger has exceeded the maximum number of allowed tickets
        if (ticketsBucket.size() < ticket.getFlight().maxSeats) { 
            ticketsBucket.add(ticket);
        } else {
            System.err.println("Exceeded the maximum number of tickets!");
        }
    }
    
    // Returns the list of tickets for this passenger
    public List<Ticket> getTickets() {
        return ticketsBucket;
    }
    
    // Displays the status of all tickets booked by the passenger
    public void viewTicketStatus(){

        if (ticketsBucket.isEmpty()) {
            System.out.println("You haven't booked any flights yet.");
        } else {
            for (int i = 0; i < ticketsBucket.size(); i++) {
                String flightID = ticketsBucket.get(i).getFlight().flightID;
                String ticketStatus = ticketsBucket.get(i).getStatus();
                System.out.println("Flight ID: " + flightID
                        + "\nTicket Status: " + ticketStatus);
            }
        }
    }

    // Edits the passenger's name and passport number
    public void editPassengerDetails(String newName, String newPassportNumber){
        if(newName != null && !newName.isEmpty()){
            this.name = newName;
        }
        if(newPassportNumber != null && !newPassportNumber.isEmpty()){
            this.passportNum = newPassportNumber; 
        }   
        System.out.println("Passenger details updated successfully.");
    }   
    
    // Overrides the toString() method to provide a formatted string representation
    @Override
    public String toString(){
        return "Passenger Information: "+
                "\nName: "+this.name+
                "\nPassport Number: "+this.passportNum;
    }
}