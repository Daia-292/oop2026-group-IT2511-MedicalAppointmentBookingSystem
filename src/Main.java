import Business.AppointmentService;
import edu.aitu.oop3.db.DatabaseConnection;
import repository.PostgresAppointmentRepository;
import UI.ConsoleUI;

public class Main {
    public static void main(String[] args) {
        var db = new DatabaseConnection();
        var repo = new PostgresAppointmentRepository(db);
        var service = new AppointmentService(repo);

        new ConsoleUI(service).run();
    }
}
