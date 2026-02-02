package repository;

import edu.aitu.oop3.db.DatabaseConnection;
import entity.Appointment;
import entity.AppointmentStatus;
import entity.AppointmentType;
import entity.BasicAppointment;
import entity.FollowUpAppointment;
import entity.InPersonAppointment;
import entity.OnlineAppointment;

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
        Appointment appt = new BasicAppointment(
                0,
                patientId,
                doctorId,
                startAt,
                endAt,
                AppointmentStatus.BOOKED,
                LocalDateTime.now()
        );
        return create(appt);
    }

    @Override
    public Appointment create(Appointment appt) {
        String sql = """
            INSERT INTO appointments
                (patient_id, doctor_id, start_at, end_at, status, type, meeting_link, room, previous_appointment_id, note)
            VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING id, created_at
            """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, appt.getPatientId());
            ps.setLong(2, appt.getDoctorId());
            ps.setTimestamp(3, Timestamp.valueOf(appt.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(appt.getEndTime()));
            ps.setString(5, appt.getStatus().name());
            ps.setString(6, appt.getType().name());

            // defaults for nullable typed fields
            String meetingLink = null;
            String room = null;
            Long previousAppointmentId = null;
            String note = null;

            if (appt.getType() == AppointmentType.ONLINE) {
                meetingLink = ((OnlineAppointment) appt).getMeetingLink();
            } else if (appt.getType() == AppointmentType.IN_PERSON) {
                room = ((InPersonAppointment) appt).getRoom();
            } else if (appt.getType() == AppointmentType.FOLLOW_UP) {
                // предполагаемые имена геттеров в FollowUpAppointment:
                previousAppointmentId = ((FollowUpAppointment) appt).getPreviousAppointmentId();
                note = ((FollowUpAppointment) appt).getNote();
            }

            ps.setString(7, meetingLink);
            ps.setString(8, room);

            if (previousAppointmentId == null) {
                ps.setNull(9, Types.BIGINT);
            } else {
                ps.setLong(9, previousAppointmentId);
            }

            ps.setString(10, note);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Возвращаем объект из БД (чтобы id/createdAt были актуальны)
                    return new BasicAppointment(
                            rs.getLong("id"),
                            appt.getPatientId(),
                            appt.getDoctorId(),
                            appt.getStartTime(),
                            appt.getEndTime(),
                            appt.getStatus(),
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
            SELECT id, patient_id, doctor_id, start_at, end_at, status, created_at,
                   type, meeting_link, room, previous_appointment_id, note
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
        long id = rs.getLong("id");
        long patientId = rs.getLong("patient_id");
        long doctorId = rs.getLong("doctor_id");
        LocalDateTime startAt = rs.getTimestamp("start_at").toLocalDateTime();
        LocalDateTime endAt = rs.getTimestamp("end_at").toLocalDateTime();
        AppointmentStatus status = AppointmentStatus.valueOf(rs.getString("status").toUpperCase());
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        String typeRaw = rs.getString("type");
        AppointmentType type = (typeRaw == null)
                ? AppointmentType.BASIC
                : AppointmentType.valueOf(typeRaw.toUpperCase());

        return switch (type) {
            case ONLINE -> new OnlineAppointment(
                    id, patientId, doctorId,
                    startAt, endAt,
                    status, createdAt,
                    rs.getString("meeting_link")
            );
            case IN_PERSON -> new InPersonAppointment(
                    id, patientId, doctorId,
                    startAt, endAt,
                    status, createdAt,
                    rs.getString("room")
            );
            case FOLLOW_UP -> new FollowUpAppointment(
                    id, patientId, doctorId,
                    startAt, endAt,
                    status, createdAt,
                    rs.getLong("previous_appointment_id"),
                    rs.getString("note")
            );
            case BASIC -> new BasicAppointment(id, patientId, doctorId, startAt, endAt, status, createdAt);
        };
    }
}
