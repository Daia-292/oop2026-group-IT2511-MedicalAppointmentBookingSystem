package UI;

import Business.AppointmentService;
import entity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final AppointmentService service;

    public ConsoleUI(AppointmentService service) {
        this.service = service;
    }

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                printMenu();
                int choice = readInt(sc, "Choose option: ");

                switch (choice) {
                    case 1 -> handleBookBasic(sc);
                    case 2 -> handleBookTyped(sc);     // NEW
                    case 3 -> handleCancel(sc);
                    case 4 -> handleDoctorSchedule(sc);
                    case 5 -> handlePatientUpcoming(sc);
                    case 0 -> { return; }
                    default -> System.out.println("Unknown option. Try again.");
                }

                System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("Appointment Menu");
        System.out.println("1) Book appointment (basic)");
        System.out.println("2) Book appointment (ONLINE / IN_PERSON / FOLLOW_UP)"); // NEW
        System.out.println("3) Cancel appointment");
        System.out.println("4) View doctor's schedule");
        System.out.println("5) View patient's upcoming appointments");
        System.out.println("0) Exit");
    }

    private void handleBookBasic(Scanner sc) {
        int patientId = readInt(sc, "Patient ID: ");
        int doctorId = readInt(sc, "Doctor ID: ");
        LocalDateTime startAt = readDateTime(sc, "Start (yyyy-MM-ddTHH:mm:ss): ");
        LocalDateTime endAt = readDateTime(sc, "End   (yyyy-MM-ddTHH:mm:ss): ");

        try {
            var appt = service.book(patientId, doctorId, startAt, endAt);
            System.out.println("Booked appointment id: " + appt.getId());
        } catch (RuntimeException ex) {
            System.out.println("Booking failed: " + ex.getMessage());
        }
    }

    private void handleBookTyped(Scanner sc) {
        System.out.println("Choose type: 1) ONLINE  2) IN_PERSON  3) FOLLOW_UP");
        int t = readInt(sc, "Type: ");

        int patientId = readInt(sc, "Patient ID: ");
        int doctorId = readInt(sc, "Doctor ID: ");
        LocalDateTime startAt = readDateTime(sc, "Start (yyyy-MM-ddTHH:mm:ss): ");
        LocalDateTime endAt = readDateTime(sc, "End   (yyyy-MM-ddTHH:mm:ss): ");

        try {
            Appointment created;

            if (t == 1) {
                String link = readNonBlank(sc, "meetingLink: ");
                // If you have: service.bookOnline(...), call that.
                // Otherwise create entity + service.save/create.
                created = service.bookOnline(patientId, doctorId, startAt, endAt, link);
            } else if (t == 2) {
                String room = readNonBlank(sc, "room: ");
                created = service.bookInPerson(patientId, doctorId, startAt, endAt, room);
            } else if (t == 3) {
                long prevId = readLong(sc, "previousAppointmentId: ");
                String note = readLine(sc, "note (optional): ");
                created = service.bookFollowUp(patientId, doctorId, startAt, endAt, (int) prevId, note);
            } else {
                System.out.println("Invalid type.");
                return;
            }

            System.out.println("Booked: id=" + created.getId() + ", type=" + created.getType());
        } catch (RuntimeException ex) {
            System.out.println("Booking failed: " + ex.getMessage());
        }
    }

    private void handleCancel(Scanner sc) {
        int id = readInt(sc, "Appointment ID to cancel: ");
        try {
            service.cancel(id);
            System.out.println("Canceled appointment: " + id);
        } catch (RuntimeException ex) {
            System.out.println("Cancel failed: " + ex.getMessage());
        }
    }

    private void handleDoctorSchedule(Scanner sc) {
        int doctorId = readInt(sc, "Doctor ID: ");
        try {
            List<Appointment> list = service.doctorSchedule(doctorId);
            if (list.isEmpty()) {
                System.out.println("No appointments found for doctor " + doctorId);
                return;
            }
            for (Appointment a : list) System.out.println(formatAppointment(a));
        } catch (RuntimeException ex) {
            System.out.println("Failed to load schedule: " + ex.getMessage());
        }
    }

    private void handlePatientUpcoming(Scanner sc) {
        int patientId = readInt(sc, "Patient ID: ");
        try {
            List<Appointment> list = service.patientUpcoming(patientId);
            if (list.isEmpty()) {
                System.out.println("No upcoming appointments for patient " + patientId);
                return;
            }
            for (Appointment a : list) System.out.println(formatAppointment(a));
        } catch (RuntimeException ex) {
            System.out.println("Failed to load upcoming visits: " + ex.getMessage());
        }
    }

    private String formatAppointment(Appointment a) {
        String base = "id=" + a.getId()
                + ", type=" + a.getType()
                + ", patientId=" + a.getPatientId()
                + ", doctorId=" + a.getDoctorId()
                + ", start=" + a.getStartTime()
                + ", end=" + a.getEndTime()
                + ", status=" + a.getStatus();

        if (a instanceof OnlineAppointment oa) return base + ", meetingLink=" + oa.getMeetingLink();
        if (a instanceof InPersonAppointment ip) return base + ", room=" + ip.getRoom();
        if (a instanceof FollowUpAppointment fu) return base + ", prevId=" + fu.getPreviousAppointmentId() + ", note=" + fu.getNote();
        return base;
    }

    private int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid integer."); }
        }
    }

    private long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try { return Long.parseLong(s); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid long number."); }
        }
    }

    private String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    private String readNonBlank(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine();
            if (s != null && !s.isBlank()) return s.trim();
            System.out.println("Value cannot be blank.");
        }
    }

    private LocalDateTime readDateTime(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME);
            } catch (RuntimeException e) {
                System.out.println("Invalid datetime. Example: 2026-02-01T10:30");
            }
        }
    }
}
