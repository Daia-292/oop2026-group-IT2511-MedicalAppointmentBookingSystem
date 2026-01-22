import Business.AppointmentService;
import edu.aitu.oop3.db.DatabaseConnection;
import Repository.PostgresAppointmentRepository;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        var db = new DatabaseConnection();
        var repo = new PostgresAppointmentRepository(db);
        var service = new AppointmentService(repo);

        var appt = service.book(
                1, 1,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(30)
        );

        System.out.println("Booked appointment id = " + appt.getId());
        System.out.println("Doctor schedule size = " + service.doctorSchedule(1).size());
    }
}
