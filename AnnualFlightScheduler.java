import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class AnnualFlightScheduler {
    static HashMap<LocalDate, ArrayList<Flight>> flightsByDate; // Stores flights organized by date
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Date formatter for parsing and formatting dates
    private static final String FILE_NAME = "C:\\Users\\Austin\\Desktop\\flight\\flights.csv"; // Path to the CSV file storing flight data

    public AnnualFlightScheduler() {
        flightsByDate = new HashMap<>();
        loadFlightsFromCsv(); // Load flight data from the CSV file when the scheduler is created
    }

    // Loads flight data from the CSV file
    private void loadFlightsFromCsv() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            reader.readLine(); // Skip the header line

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length >= 8) {
                    String flightID = parts[0];
                    LocalDate date;
                    String confirmedPassengersStr = parts[3];
                    String waitlistedPassengersStr = parts[4];

                    try {
                        date = LocalDate.parse(parts[1]); // Parse the date from the CSV
                    } catch (Exception e) {
                        System.err.println("Invalid date format in CSV: " + parts[1]);
                        continue; // Skip this line if the date format is invalid
                    }

                    int confirmedSeats = 0;
                    int emptySeats = Flight.maxSeats;

                    try {
                        confirmedSeats = Integer.parseInt(parts[5]);
                        emptySeats = Integer.parseInt(parts[6]);

                        // Check for data inconsistencies in seat numbers
                        if (confirmedSeats + emptySeats != Flight.maxSeats || emptySeats < 0) {
                            System.err.println("Seat data inconsistency detected in flight: " + flightID);
                            confirmedSeats = Math.min(confirmedSeats, Flight.maxSeats);
                            emptySeats = Flight.maxSeats - confirmedSeats;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Failed to parse seat numbers for flight: " + flightID);
                        confirmedSeats = 0;
                        emptySeats = Flight.maxSeats; 
                    }

                    Flight flight = new Flight(flightID);
                    try {
                        flight.setConfirmedSeats(confirmedSeats);
                        flight.setEmptySeats(emptySeats);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid seat values for flight: " + flightID + ". Skipping this flight.");
                        continue; // Skip this flight if seat values are invalid
                    }

                    // Parse the lists of confirmed and waitlisted passengers
                    List<Passenger> confirmedPassengersList = parsePassengersList(confirmedPassengersStr);
                    List<Passenger> waitlistedPassengersList = parsePassengersList(waitlistedPassengersStr);

                    flight.confirmedTicketList.addAll(confirmedPassengersList);
                    flight.waitingList.addAll(waitlistedPassengersList);
                    
                    flightsByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(flight); // Add the flight to the map
                    flight.processWaitlist(); // Process the waitlist for this flight
                    
                    // Create and assign tickets to passengers
                    for (Passenger p : flight.confirmedTicketList) {
                        Ticket ticket = new Ticket(p, flight);
                        ticket.setStatus("confirmed");
                        p.addTicketToBucket(ticket);
                    }
                    
                    for (Passenger p : flight.waitingList) {
                        Ticket ticket = new Ticket(p, flight);
                        ticket.setStatus("waiting list");
                        p.addTicketToBucket(ticket);
                    }

                } else {
                    System.err.println("CSV format error: insufficient fields in line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read CSV file: " + e.getMessage());
        }
    }

    // Parses a string of passenger data into a list of Passenger objects
    private List<Passenger> parsePassengersList(String passengersStr) {
        List<Passenger> passengerList = new ArrayList<>();
        if (passengersStr != null && !passengersStr.trim().isEmpty()) {
            String[] passengers = passengersStr.split(";");
            for (String passengerStr : passengers) {
                passengerStr = passengerStr.trim();
                if (!passengerStr.isEmpty()) {
                    int startIndex = passengerStr.indexOf("(");
                    int endIndex = passengerStr.indexOf(")");

                    if (startIndex > 0 && endIndex > startIndex) {
                        String name = passengerStr.substring(0, startIndex).trim();
                        String passportNum = passengerStr.substring(startIndex + 1, endIndex).trim();
                        Passenger passenger = new Passenger(passportNum, name);
                        passengerList.add(passenger);
                    }
                }
            }
        }
        return passengerList;
    }


    // Searches for flights between two dates
    public static void searchFlights(LocalDate date1, LocalDate date2) {
        System.out.println("\nFlight for the Weeks: ");
        System.out.println();
        while (!date1.isAfter(date2)) {
            ArrayList<Flight> temp = flightsByDate.get(date1);
            if (temp != null) { 
                for (int i = 0; i < temp.size(); i++) {
                    System.out.println(temp.get(i));
                }
            }
            System.out.println("-------------------------------------------------------------------------");
            date1 = date1.plusDays(1);
        }
    }

    // Saves the current flight data to the CSV file
    public void saveFlightsToCsv() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            writer.write("Flight ID,Date,Status,Confirmed Passengers," +
                    "Waitlisted Passengers,Confirmed Seats,Empty Seats,Waitlist Count\n");
    
            for (LocalDate date : flightsByDate.keySet()) {
                for (Flight flight : flightsByDate.get(date)) {
                    String flightID = flight.flightID; 
    
                    String confirmedPassengersStr = getPassengerListString(flight.confirmedTicketList);
                    String waitlistedPassengersStr = getPassengerListString(flight.waitingList);
    
                    writer.write(flightID + "," + date + ",Available," +
                            confirmedPassengersStr + "," + waitlistedPassengersStr + "," +
                            flight.getConfirmedSeats() + "," + flight.getEmptySeats() + "," +
                            flight.getWaitlistCount() + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }

    // Converts a list of passengers to a string representation
    private String getPassengerListString(ArrayList<Passenger> passengers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            sb.append(passenger.getName()).append("(").append(passenger.getPassportNum()).append(")");
            if (i < passengers.size() - 1) {
                sb.append(";");  
            }
        }
        return sb.toString();
    }
    
    // Finds a ticket for a given flight and passenger (not used in the current code)
    private Ticket findTicket(Flight flight, Passenger passenger, String status) {   
        Ticket ticket = new Ticket(passenger, flight);
        ticket.setStatus(status);   
        return ticket;   
    }
    
    // Retrieves passenger information based on passport number
    public Passenger getPassengerInfo(String passportNum) {
        for (LocalDate date : flightsByDate.keySet()) {
            for (Flight flight : flightsByDate.get(date)) {
                for (Passenger p : flight.confirmedTicketList) {
                    if (p.getPassportNum().equals(passportNum)) {
                        return p;
                    }
                }
                for (Passenger p : flight.waitingList) {
                    if (p.getPassportNum().equals(passportNum)) {
                        return p;
                    }
                }
            }
        }
        return null;
    }

    // Converts a queue of passengers to a string representation (not used in the current code)
    private String getPassengerListString(Queue<Passenger> passengers) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Passenger> passengerList = new ArrayList<>(passengers); 
        for (int i = 0; i < passengerList.size(); i++) {
            Passenger passenger = passengerList.get(i);
            sb.append(passenger.getName()).append("(").append(passenger.getPassportNum()).append(")");
            if (i < passengerList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    // Processes a flight ID string to retrieve the corresponding Flight object
    public static Flight processFlightID(String flightID) {
        String[] parts = flightID.split("-");
        int flightIndex = Integer.parseInt(parts[1]);
        String dateString = parts[2] + "-" + parts[3] + "-" + parts[4];
        LocalDate date = LocalDate.parse(dateString, formatter);

        return flightsByDate.get(date).get(flightIndex);
    }
}