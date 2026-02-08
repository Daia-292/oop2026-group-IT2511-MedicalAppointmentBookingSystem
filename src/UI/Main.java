package UI;

import Business.AppointmentService;
import DataComponents.db.DatabaseConnection;
import DataComponents.repository.PostgresAppointmentRepository;

public class Main {
    public static void main(String[] args) {
        var db = new DatabaseConnection();
        var repo = new PostgresAppointmentRepository(db);
        var service = new AppointmentService(repo);

        new ConsoleUI(service).run();
    }}