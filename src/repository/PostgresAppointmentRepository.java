package repository;

import edu.aitu.oop3.db.DatabaseConnection;
import entity.Appointment;
import entity.AppointmentStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresAppointmentRepository implements AppointmentRepository {
    private final DatabaseConnection db;

    public PostgresAppointmentRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Appointment createBooked(int patientId, int doctorId, LocalDateTime startAt, LocalDateTime endAt) {
        String sql = """
            INSERT INTO appointments (patient_id, doctor_id, start_at, end_at, status)
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            ps.setTimestamp(3, Timestamp.valueOf(startAt));
            ps.setTimestamp(4, Timestamp.valueOf(endAt));
            ps.setString(5, AppointmentStatus.BOOKED.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Appointment(
                            rs.getLong("id"),
                            (long) patientId,
                            (long) doctorId,
                            startAt,
                            endAt,
                            AppointmentStatus.BOOKED,
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
            throw new RuntimeException("Failed to create appointment");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Appointment> findById(int id) {
        String sql = """
            SELECT id, patient_id, doctor_id, start_at, end_at, status, created_at
            FROM appointments
            WHERE id = ?
            """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Appointment> findByDoctor(int doctorId) {
        String sql = """
            SELECT id, patient_id, doctor_id, start_at, end_at, status, created_at
            FROM appointments
            WHERE doctor_id = ?
            ORDER BY start_at
            """;

        List<Appointment> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Appointment> findUpcomingByPatient(int patientId) {
        String sql = """
            SELECT id, patient_id, doctor_id, start_at, end_at, status, created_at
            FROM appointments
            WHERE patient_id = ?
              AND status = ?
              AND start_at >= now()
            ORDER BY start_at
            """;

        List<Appointment> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, patientId);
            ps.setString(2, AppointmentStatus.BOOKED.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasOverlapBooked(int doctorId, LocalDateTime startAt, LocalDateTime endAt) {
        String sql = """
            SELECT 1
            FROM appointments
            WHERE doctor_id = ?
              AND status = ?
              AND start_at < ?
              AND end_at > ?
            LIMIT 1
            """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ps.setString(2, AppointmentStatus.BOOKED.name());
            ps.setTimestamp(3, Timestamp.valueOf(endAt));
            ps.setTimestamp(4, Timestamp.valueOf(startAt));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancel(int id) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, AppointmentStatus.CANCELED.name());
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    private Appointment mapRow(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getLong("doctor_id"),
                rs.getTimestamp("start_at").toLocalDateTime(),
                rs.getTimestamp("end_at").toLocalDateTime(),
                AppointmentStatus.valueOf(rs.getString("status").toUpperCase()),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
