package Hotel_Reservation_System;

import java.sql.*;
import java.util.Scanner;


public class HotelReservationSystem {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    private static final String username = "root";

    private static final String password = "tiger";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{

            Connection conn = DriverManager.getConnection(url,username,password);

            System.out.println("=================================");
            welcome();
            System.out.println("=================================");


            while (true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.println("Choose an Option: ");
                int choise = sc.nextInt();
                switch (choise){
                    case 1:
                        reserveRoom(conn, sc);
                        break;
                    case 2:
                        viewReservations(conn);
                        break;
                    case 3:
                        getRoomNumber(conn, sc);
                        break;
                    case 4:
                        updateReservation(conn, sc);
                        break;
                    case 5:
                        deleteReservation(conn, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choise. Try again.");
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }


    private static void reserveRoom(Connection conn, Scanner sc){

        try{
            System.out.print("Enter guest name: ");
            String guestName = sc.next();
            sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = sc.next();

            String sql = "insert into reservations(guest_name, room_number, contact_number)" +
                    "values('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try(Statement stmt = conn.createStatement()){
                int affectedRow = stmt.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println();
                    System.out.println("Reservation successfully!");
                }else{
                    System.out.println("Reservation failed.");
                }
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }


    private static void viewReservations(Connection conn) throws SQLException{
        String sql = "select reservation_id, guest_name, room_number, contact_number, reservation_date from reservations";

        try(Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql)){

            System.out.println("Current Reservations: ");
            System.out.println("+----------------+------------------------+-------------+--------------------+--------------------+");
            System.out.println("| Reservation ID | Guest Name             | Room Number | Contact Number     | Reservation Date   |");
            System.out.println("+----------------+------------------------+-------------+--------------------+--------------------+");

            while(resultSet.next()){
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("|%-16s|%-24s|%-13s|%-20s|%-20s|\n",
                        reservationId,guestName,roomNumber,contactNumber,reservationDate);
            }

            System.out.println("+----------------+------------------------+-------------+--------------------+--------------------+");
        }
    }


    private static void getRoomNumber(Connection conn, Scanner sc){
        try{
            System.out.print("Enter reservation ID:");
            int reservationId = sc.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = sc.next();

            String sql = "select room_number from reservations " + "where reservation_id = " + reservationId +
                    " and guest_name = '" + guestName + "'";

            try(Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(sql)){

                if(resultSet.next()){
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room Number for Reservation ID " + reservationId +
                              " and Guest Name " + guestName + " is: "+ roomNumber);
                }else {
                    System.out.println("Reservation not found for the given ID and Guest name.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    private static void updateReservation(Connection conn, Scanner sc){
        try{
            System.out.print("Enter reservation ID to update: ");
            int reservationId = sc.nextInt();
            sc.nextLine();

            if(!reservationExists(conn, reservationId)){
                System.out.println("Reservation Not Found For Given ID.");
                return;
            }

            System.out.print("Enter New Guest Name: ");
            String newGuestName = sc.next();
            System.out.print("Enter New Room Number: ");
            int newRoomNumber = sc.nextInt();
            System.out.print("Enter New Contact Number: ");
            String newContactNumber = sc.next();

            String sql = "update reservations set guest_name = '" + newGuestName + "', " + "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " + " where reservation_id = " + reservationId;

            try(Statement stmt = conn.createStatement()){
                int affectedRow = stmt.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println("Reservation Update Successfully!");
                }else {
                    System.out.println("Reservation Update Failed.");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    private static void deleteReservation(Connection conn, Scanner sc){
        try{
            System.out.print("Enter Reservation ID To Delete: ");
            int reservationId = sc.nextInt();

            if(!reservationExists(conn, reservationId)){
                System.out.println("Reservation Not Found For The Given ID.");
                return;
            }

            String sql = "delete from reservations where reservation_id = " + reservationId;

            try(Statement stmt = conn.createStatement()){
                int affectedRow = stmt.executeUpdate(sql);

                if(affectedRow > 0){
                    System.out.println("Reservation Deleted Successfully!");
                }else {
                    System.out.println("Reservation Deletion Failed.");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    private static boolean reservationExists(Connection conn, int reservationId){
        try{
            String sql = "select reservation_id from reservations where reservation_id = " + reservationId;

            try(Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(sql)){

                return resultSet.next();
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }



    public static void exit() throws InterruptedException{
        String msg = "Exiting System....";

        for(char ch: msg.toCharArray()){
            System.out.print(ch);
            Thread.sleep(200);
        }
    }

    public static void welcome() throws InterruptedException{
        String msg = "\n✨ Welcome to My Hotel Reservation System ✨\n";
        for(char ch: msg.toCharArray()){
            System.out.print(ch);
            Thread.sleep(100);
        }
        System.out.println();
    }

}
