import Business.AppointmentService;
import edu.aitu.oop3.db.DatabaseConnection;
import repository.PostgresAppointmentRepository;
import entity.Appointment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

            System.out.println("Connecting to Supabase...");
            try (Connection connection = DatabaseConnection.getConnection()) {
                System.out.println("Connected successfully!");
                String sql = "SELECT CURRENT_TIMESTAMP";
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Database time: " + rs.getTimestamp(1));
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error while connecting to database:");
                e.printStackTrace();
            }

        var db = new DatabaseConnection();
        var repo = new PostgresAppointmentRepository(db);
        var service = new AppointmentService(repo);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                int choice = readInt(sc, "Choose option: ");

                switch (choice) {
                    case 1 -> handleBook(sc, service);
                    case 2 -> handleCancel(sc, service);
                    case 3 -> handleDoctorSchedule(sc, service);
                    case 4 -> handlePatientUpcoming(sc, service);
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }

                System.out.println();
            }
        }
    }

    private static void printMenu() {
        System.out.println("Appointment Menu");
        System.out.println("1) Book appointment");
        System.out.println("2) Cancel appointment");
        System.out.println("3) View doctor's schedule");
        System.out.println("4) View patient's upcoming appointments");
        System.out.println("0) Exit");
    }

    private static void handleBook(Scanner sc, AppointmentService service) {
        int patientId = readInt(sc, "Patient ID: ");
        int doctorId = readInt(sc, "Doctor ID: ");
        LocalDateTime startAt = readDateTime(sc, "Start (yyyy-MM-dd HH:mm): ");
        LocalDateTime endAt = readDateTime(sc, "End   (yyyy-MM-dd HH:mm): ");

        try {
            var appt = service.book(patientId, doctorId, startAt, endAt);
            System.out.println("Booked appointment id: " + appt.getId());
        } catch (RuntimeException ex) {
            System.out.println("Booking failed: " + ex.getMessage());
        }
    }

    private static void handleCancel(Scanner sc, AppointmentService service) {
        int id = readInt(sc, "Appointment ID to cancel: ");
        try {
            service.cancel(id);
            System.out.println("Canceled appointment: " + id);
        } catch (RuntimeException ex) {
            System.out.println("Cancel failed: " + ex.getMessage());
        }
    }

    private static void handleDoctorSchedule(Scanner sc, AppointmentService service) {
        int doctorId = readInt(sc, "Doctor ID: ");
        try {
            List<Appointment> list = service.doctorSchedule(doctorId);
            if (list.isEmpty()) {
                System.out.println("No appointments found for doctor " + doctorId);
                return;
            }
            for (Appointment a : list) {
                System.out.println(formatAppointment(a));
            }
        } catch (RuntimeException ex) {
            System.out.println("Failed to load schedule: " + ex.getMessage());
        }
    }

    private static void handlePatientUpcoming(Scanner sc, AppointmentService service) {
        int patientId = readInt(sc, "Patient ID: ");
        try {
            List<Appointment> list = service.patientUpcoming(patientId);
            if (list.isEmpty()) {
                System.out.println("No upcoming appointments for patient " + patientId);
                return;
            }
            for (Appointment a : list) {
                System.out.println(formatAppointment(a));
            }
        } catch (RuntimeException ex) {
            System.out.println("Failed to load upcoming visits: " + ex.getMessage());
        }
    }

    private static String formatAppointment(Appointment a) {
        // Works even if your Appointment getters return String/enum for status
        return "id=" + a.getId()
                + ", patientId=" + a.getPatientId()
                + ", doctorId=" + a.getDoctorId()
                + ", start=" + a.getStartTime()
                + ", end=" + a.getEndTime()
                + ", status=" + a.getStatus();
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static LocalDateTime readDateTime(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
            } catch (RuntimeException e) {
                System.out.println("Invalid datetime. Example: 2026-02-01 10:30");
            }
        }
    }
}



