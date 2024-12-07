import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateFlightsCsv {

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Austin\\Desktop\\datastruc-main\\"; 
        String fileName = "flights.csv";

        HashMap<LocalDate, ArrayList<String>> flightsByDate = new HashMap<>();

        LocalDate startDate = LocalDate.of(2024, 12, 5);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        while (!startDate.isAfter(endDate)) {
            ArrayList<String> dailyFlights = new ArrayList<>(); 
            for (int i = 0; i < 3; i++) {
                String flightID = "Flight-" + i + "-" + startDate; 
                dailyFlights.add(flightID); 
            }
            flightsByDate.put(startDate, dailyFlights); 
            startDate = startDate.plusDays(1);
        }

        try (FileWriter writer = new FileWriter(filePath + fileName)) {
            writer.write("Flight ID,Date,Status,Confirmed Passengers," +
                    "Waitlisted Passengers,Confirmed Seats,Empty Seats,Waitlist Count\n");

            for (LocalDate date : flightsByDate.keySet()) {
                for (int i = 0; i < 3; i++) { 
                    String flightID = "Flight-" + i + "-" + date; 

                    writer.write(flightID + "," + date + ",Available," + 
                            "" + "," + "" + "," + 
                            0 + "," + 5 + "," + 0 + "\n"); 
                }
            }

            System.out.println("The CSV file was generated:" + filePath + fileName);
        } catch (IOException e) {
            System.err.println("Failed to write CSV file: " + e.getMessage());
        }
    }
}