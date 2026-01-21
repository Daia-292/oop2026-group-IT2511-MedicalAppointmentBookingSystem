package app;

import edu.aitu.oop3.db.DatabaseConnection;
import edu.aitu.oop3.db.IDB;
import edu.aitu.oop3.db.PostgresDB;
import entities.Appointment;
import exceptions.ConflictException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import repositories.impl.AppointmentRepositoryImpl;
import repositories.interfaces.AppointmentRepository;
import services.AppointmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // DB init (IDB + Postgres implementation) :contentReference[oaicite:6]{index=6}
        String password = DbConfig.loadPassword(); // config.properties approach :contentReference[oaicite:7]{index=7}
        IDB db = new PostgresDB(DbConfig.URL, DbConfig.USER, password);

        AppointmentRepository appointmentRepo = new AppointmentRepositoryImpl(db);
        AppointmentService appointmentService = new AppointmentService(appointmentRepo);

        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> bookAppointmentUI(sc, appointmentService);
                    case "2" -> cancelAppointmentUI(sc, appointmentService);
                    case "3" -> doctorScheduleUI(sc, appointmentService);
                    case "4" -> patientAppointmentsUI(sc, appointmentService);
                    case "5" -> findAppointmentByIdUI(sc, appointmentService);
                    case "0" -> {
                        System.out.println("Bye!");
                        return;
                    }
                    default -> System.out.println("Unknown option. Try again.");
                }
            } catch (ValidationException | NotFoundException | ConflictException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            } catch (Exception ex) {
                // на защите покажете, что вы различаете ожидаемые ошибки и неожиданные :contentReference[oaicite:8]{index=8}
                System.out.println("UNEXPECTED ERROR: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("=== Medical Appointment Booking (Console Demo) ===");
        System.out.println("1) Book appointment");
        System.out.println("2) Cancel appointment");
        System.out.println("3) View doctor's schedule");
        System.out.println("4) View patient's appointments");
        System.out.println("5) Find appointment by id");
        System.out.println("0) Exit");
        System.out.print("Choose: ");
    }

    private static void bookAppointmentUI(Scanner sc, AppointmentService service) {
        System.out.print("Patient ID: ");
        int patientId = readInt(sc);

        System.out.print("Doctor ID: ");
        int doctorId = readInt(sc);

        System.out.print("Time slot (yyyy-MM-dd HH:mm): ");
        LocalDateTime slot = LocalDateTime.parse(sc.nextLine().trim(), FMT);

        Appointment a = service.book(patientId, doctorId, slot);
        System.out.println("Booked: " + a);
    }

    private static void cancelAppointmentUI(Scanner sc, AppointmentService service) {
        System.out.print("Appointment ID to cancel: ");
        int apptId = readInt(sc);

        service.cancel(apptId);
        System.out.println("Canceled appointment id=" + apptId);
    }

    private static void doctorScheduleUI(Scanner sc, AppointmentService service) {
        System.out.print("Doctor ID: ");
        int doctorId = readInt(sc);

        List<Appointment> list = service.doctorSchedule(doctorId);
        if (list.isEmpty()) {
            System.out.println("No appointments for doctor id=" + doctorId);
            return;
        }
        list.forEach(System.out::println);
    }

    private static void patientAppointmentsUI(Scanner sc, AppointmentService service) {
        System.out.print("Patient ID: ");
        int patientId = readInt(sc);

        List<Appointment> list = service.patientAppointments(patientId);
        if (list.isEmpty()) {
            System.out.println("No appointments for patient id=" + patientId);
            return;
        }
        list.forEach(System.out::println);
    }

    private static void findAppointmentByIdUI(Scanner sc, AppointmentService service) {
        System.out.print("Appointment ID: ");
        int id = readInt(sc);

        Appointment a = service.getById(id);
        System.out.println(a);
    }

    private static int readInt(Scanner sc) {
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new ValidationException("Expected integer, got: " + s);
        }
    }
}
