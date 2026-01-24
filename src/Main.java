import Business.AppointmentService;
import edu.aitu.oop3.db.DatabaseConnection;
import Repository.PostgresAppointmentRepository;
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

        var appt = service.book(
                1, 1,
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(0),
                LocalDateTime.now().plusDays(1).withHour(10).withMinute(30)
        );

        System.out.println("Booked appointment id: " + appt.getId());
        System.out.println("Doctor schedule size = " + service.doctorSchedule(1).size());
    }
}
