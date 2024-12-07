public class Ticket {

    Flight flight; // The flight associated with this ticket
    Passenger passenger; // The passenger holding this ticket
    private String status; // The status of the ticket (e.g., "confirmed", "waiting list", "canceled")

    public Ticket(Passenger passenger, Flight flight){
        this.passenger = passenger;
        this.flight = flight;
    }

    // Getters and setters for status, passenger, and flight
    public String getStatus() {
        return status;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }
}