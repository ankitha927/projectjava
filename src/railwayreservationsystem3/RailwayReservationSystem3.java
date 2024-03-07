package railwayreservationsystem3;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class PassengerDetails {
    private String name;
    private int age;
    private String gender;
    private String ticketNumber;

    public PassengerDetails(String name, int age, String gender, String ticketNumber) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.ticketNumber = ticketNumber;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }
}

class TicketPrice {
    private double acPrice;
    private double nonAcPrice;

    public TicketPrice(double acPrice, double nonAcPrice) {
        this.acPrice = acPrice;
        this.nonAcPrice = nonAcPrice;
    }

    public double calculatePrice(String seatType, int age) {
        double price = (seatType.equalsIgnoreCase("AC")) ? acPrice : nonAcPrice;
        if (age < 18) {
            // Apply discount for passengers below 18
            price /= 2;
        }
        return price;
    }
}

class TicketNumber {
    private static int ticketCounter = 1000;

    public static String generateTicketNumber() {
        return "T" + ticketCounter++;
    }
}

class Payment {
    public static boolean processPayment(double totalPrice) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Total Price for all tickets: Rs" + totalPrice);
        System.out.println("Select payment method:");
        System.out.println("1. Card Payment");
        System.out.println("2. GPay");
        int paymentMethod = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (paymentMethod) {
            case 1:
                return processCardPayment(totalPrice, scanner);
            case 2:
                return processGPay(totalPrice, scanner);
            default:
                System.out.println("Invalid payment method selected.");
                return false;
        }
    }

    private static boolean processCardPayment(double totalPrice, Scanner scanner) {
        System.out.print("Enter Card Number: ");
        String cardNumber = scanner.nextLine();
        System.out.print("Enter Expiry Date (MM/YY): ");
        String expiryDate = scanner.nextLine();
        System.out.print("Enter CVV: ");
        String cvv = scanner.nextLine();
        System.out.print("Enter Card Holder's Name: ");
        String cardHolderName = scanner.nextLine();
        System.out.print("Enter Card Password: ");
        String cardPassword = scanner.nextLine();

        // Validate card details and password
        // Assuming the validation is done here

        // Simulate payment processing
        System.out.println("Processing card payment...");
        // Simulated success, replace with actual payment processing logic
        System.out.println("Payment successful!");
        return true;
    }

    private static boolean processGPay(double totalPrice, Scanner scanner) {
        System.out.print("Enter GPay UPI ID: ");
        String upiId = scanner.nextLine();
        System.out.print("Enter GPay PIN: ");
        String gpayPin = scanner.nextLine();

        // Validate UPI ID and PIN
        // Assuming the validation is done here

        // Simulate payment processing
        System.out.println("Processing GPay payment...");
        // Simulated success, replace with actual payment processing logic
        System.out.println("Payment successful!");
        return true;
    }
}

public class RailwayReservationSystem3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the ABH Train Booking System!");
        System.out.println("Enter your starting point:");
        String sourceStation = scanner.nextLine();
        System.out.println("Enter your Destination:");
        String destinationStation = scanner.nextLine();
        String jdbcUrl = "jdbc:mysql://localhost:3306/new_train";
        String username = "root";
        String password = "";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String query = "SELECT * FROM newtrain_details WHERE arrival_station = ? AND departure_destination = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, sourceStation);
            preparedStatement.setString(2, destinationStation);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            List<String[]> trainDetails = new ArrayList<>();
            while (resultSet.next()) {
                String[] details = new String[100];
                details[0] = resultSet.getString("train_name");
                details[1] = String.valueOf(resultSet.getInt("ac_seats"));
                details[2] = String.valueOf(resultSet.getInt("non_ac_seats"));
                details[3] = String.valueOf(resultSet.getDouble("ticket_price(ac)"));
                details[4] = String.valueOf(resultSet.getDouble("ticket_price(non ac)"));
                details[5] = resultSet.getString("arrival_time");
                details[6] = resultSet.getString("departure_time");
                details[7] = resultSet.getString("arrival_station");
                details[8] = resultSet.getString("departure_destination");
                details[9] =resultSet.getString("train_id");

                
                trainDetails.add(details);
            }
            
            if (trainDetails.isEmpty()) {
                System.out.println("No trains available for the specified route.");
                return;
            } else {
                System.out.println("Available Trains and Seats:");
                int trainCount = 0;
                for (String[] details : trainDetails) {
                    trainCount++;
                    System.out.println(trainCount + ". Train: " + details[0]);
                    System.out.println("   AC Seats: " + details[1]);
                    System.out.println("   Non-AC Seats: " + details[2]);
                    System.out.println("   AC Price: Rs" + details[3]);
                    System.out.println("   Non-AC Price: Rs" + details[4]);
                    System.out.println("   Arrival Time: " + details[5]);
                    System.out.println("   Departure Time: " + details[6]);
                    System.out.println();
                }

                System.out.print("Choose the train you want to travel with (1-" + trainCount + "): ");
                int trainChoice = scanner.nextInt();
                if (trainChoice < 1 || trainChoice > trainCount) {
                    System.out.println("Invalid train choice. Please try again.");
                    return;
                }

                String[] selectedTrainDetails = trainDetails.get(trainChoice - 1);
                System.out.println("You have selected Train: " + selectedTrainDetails[0]);
                System.out.println();
            }
            
            System.out.print("How many tickets do you want to book? ");
            int numTickets = scanner.nextInt();
            List<PassengerDetails> passengers = new ArrayList<>();
            scanner.nextLine(); // Consume newline
            double totalPrice = 0;
            for (int i = 1; i <= numTickets; i++) {
                System.out.println("\nEnter details for Passenger " + i + ":");
                System.out.print("Please enter the Name: ");
                String name = scanner.nextLine();
                System.out.print("Please enter the Gender: ");
                String gender = scanner.nextLine();
                System.out.print("Please enter the Age: ");
                int age = scanner.nextInt();
                scanner.nextLine();
                String ticketNumber = TicketNumber.generateTicketNumber();
                passengers.add(new PassengerDetails(name, age, gender, ticketNumber));
            }
            
            if (!trainDetails.isEmpty()) {
                for (int i = 1; i <= numTickets; i++) {
                    String[] details = trainDetails.get(0);
                    double acPrice = Double.parseDouble(details[3]);
                    double nonAcPrice = Double.parseDouble(details[4]);
                    TicketPrice ticketPrice = new TicketPrice(acPrice, nonAcPrice);
                    System.out.print("\nEnter seat type for Ticket " + i + " (1 for AC, 2 for Non-AC): ");
                    int seatTypeChoice = scanner.nextInt();
                    String seatType = (seatTypeChoice == 1) ? "AC" : "Non-AC";
                    System.out.println("You selected: " + seatType + " seat for Ticket " + i);
                    double price = ticketPrice.calculatePrice(seatType, passengers.get(i - 1).getAge());
                    totalPrice += price;
                    System.out.println("Ticket " + i + " Price: Rs" + price);
                    System.out.println("Ticket " + i + " Number: " + passengers.get(i - 1).getTicketNumber());
                }
            }
            
            if (Payment.processPayment(totalPrice)) {
                System.out.println("Thank you for booking with ABH Train Booking System!");
            } else {
                System.out.println("Payment failed. Please try again.");
            }
            
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO trans2 (train_id, train_name, passenger_name, arrival_station, departure_destination, ticket_price) VALUES (?, ?, ?, ?, ?, ?)");

            // Get the selected train details
            String[] selectedTrainDetails = trainDetails.get(0);
            String trainName = selectedTrainDetails[0];
            String arrivalStation = selectedTrainDetails[7];
            String departureDestination = selectedTrainDetails[8];
            double ticketPrice = Double.parseDouble(selectedTrainDetails[3]); // Assuming ticket price is a double

            // Insert passenger details into the database
            for (PassengerDetails passenger : passengers) {
                // Insert the train id (assuming it's unique for each train, otherwise, generate a unique ID)
                //String trainId = selectedTrainDetails[0].substring(0, 3); // Example: Extracting train ID from train name
              // insertStatement.setString(1, trainId);
             String trainId = selectedTrainDetails[9]; // Assuming train_id is stored at index 0 in selectedTrainDetails
insertStatement.setString(1, trainId);


                // Extracting train ID from train name and parsing it into an integer
               

                // Insert the train name
                insertStatement.setString(2, trainName);

                // Insert passenger name
                insertStatement.setString(3, passenger.getName());

                // Insert arrival station
                insertStatement.setString(4, arrivalStation);

                // Insert departure destination
                insertStatement.setString(5, departureDestination);

                // Insert ticket price
                insertStatement.setDouble(6, ticketPrice);

                // Execute the insert statement
                insertStatement.executeUpdate();
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
            insertStatement.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
