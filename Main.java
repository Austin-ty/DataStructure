import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static AnnualFlightScheduler afs; // Instance of the flight scheduler

    public static void main(String[] args) {
        afs = new AnnualFlightScheduler(); // Initialize the flight scheduler
        Scanner sc = new Scanner(System.in);
        int choice; 

        do { 
            // Display the main menu
            System.out.println("Welcome to Flight Booking System"); 
            System.out.println("""
                    MENU:
                    (1): Search Flight
                    (2): Book Ticket
                    (3): Edit Ticket Information
                    (4): View Ticket Status
                    (5): Cancel a Ticket
                    (6): Exit
                                                            
                    Enter your choice here:
                    """);
            choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1: {
                    Main.SearchFlightForWeek(); // Search for flights within a date range
                    break;
                }
                case 2: {
                    Main.BookTicket(); // Book a ticket for a passenger
                    break;
                }
                case 3:
                    Main.EditTicketInformation(); // Edit passenger information
                    Main.afs.saveFlightsToCsv(); // Save changes to the CSV
                    break;
                case 4: {
                    System.out.println("Please enter your passport number:");
                    String passportNum = sc.nextLine();
                
                    Passenger passenger = afs.getPassengerInfo(passportNum); // Retrieve passenger information
                
                    if (passenger != null) {
                        passenger.viewTicketStatus(); // Display ticket status for the passenger
                    } else {
                        System.out.println("The passenger information could not be found.");
                    }
                    break;
                }
                case 5:{
                    Main.CancelTicket(); // Cancel a booked ticket
                    break;
                }   
            }
        } while (choice != 6); 

        System.out.println("Thanks for using!");
    }

    // Edits passenger information (name and passport number)
    public static void EditTicketInformation(){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your current passport number");
        String passportNum = s.nextLine();
    
        Passenger passenger = afs.getPassengerInfo(passportNum); // Retrieve passenger information
    
        if(passenger != null){
            System.out.println("Enter new name (press Enter to skip):");
            String newName = s.nextLine();
            System.out.println("Enter new passport number (press Enter to skip):");
            String newPassportNum = s.nextLine();
            
            passenger.editPassengerDetails(newName, newPassportNum); // Update passenger details
            System.out.println("Passenger details updated successfully.");
        }else{
            System.out.println("Passenger not found");
        }
    }
    
    // Books a ticket for a flight
    public static void BookTicket() { 
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the flight ID (format: Flight-<number>-<yyyy-MM-dd>):");
        String flightID = sc.nextLine();
        System.out.println("Please enter your name:");
        String name = sc.nextLine();
        System.out.println("Please enter your passport number:");
        String passportNum = sc.nextLine();

        Flight flight = AnnualFlightScheduler.processFlightID(flightID); // Get the Flight object

        Passenger passenger = new Passenger(passportNum, name); // Create a Passenger object

        flight.bookFlight(passenger, flight); // Book the flight for the passenger

    }

    // Cancels a booked ticket
    public static void CancelTicket(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the flight ID:");
        String flightID = sc.nextLine();
        System.out.println("Enter your passport number:");
        String passportNum = sc.nextLine();
        
        Flight flight = AnnualFlightScheduler.processFlightID(flightID); // Get the Flight object
        Passenger passenger = afs.getPassengerInfo(passportNum); // Retrieve passenger information
        
        if(passenger != null && flight != null){
            boolean result = flight.cancelTicket(passenger); // Cancel the ticket
            if(result){
                System.out.println("Ticket successfully canceled.");
                afs.saveFlightsToCsv(); // Save changes to CSV
            }else{
                System.out.println("Failed to cancel the ticket. Please check your information.");
            }
        }else{
            System.out.println("Flight or passenger not found.");
        }
    }
    
    // Searches for flights within a specified date range
    public static void SearchFlightForWeek(){
        Scanner sc=new Scanner(System.in);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("Enter a starting date:");
        LocalDate date1=LocalDate.parse(sc.nextLine(),formatter);
        System.out.println("Enter an ending date:");
        LocalDate date2=LocalDate.parse(sc.nextLine(),formatter);

        AnnualFlightScheduler.searchFlights(date1,date2);
    }

}