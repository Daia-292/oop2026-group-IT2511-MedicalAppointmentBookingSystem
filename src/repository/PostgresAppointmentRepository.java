package repository;

import edu.aitu.oop3.db.IDB;
import entity.Appointment;
import entity.AppointmentStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresAppointmentRepository implements AppointmentRepository {

    private final IDB db;

    public PostgresAppointmentRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Appointment save(Appointment appointment) {
        String sql = "INSERT INTO appointments(patient_id, doctor_id, start_time, status) " +
                     "VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, appointment.getPatientId());
            st.setLong(2, appointment.getDoctorId());
            st.setTimestamp(3, Timestamp.valueOf(appointment.getStartTime()));
            st.setString(4, appointment.getStatus().name());

            ResultSet rs = st.executeQuery();
            if (rs.next()) appointment.setId(rs.getLong("id"));
            return appointment;
        } catch (SQLException e) {
            throw new RuntimeException("Appointment save error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Appointment> findById(long id) {
        String sql = "SELECT id, patient_id, doctor_id, start_time, status FROM appointments WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Appointment findById error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> findByDoctorId(long doctorId) {
        String sql = "SELECT id, patient_id, doctor_id, start_time, status " +
                     "FROM appointments WHERE doctor_id = ? ORDER BY start_time";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, doctorId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Appointment findByDoctorId error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Appointment> findUpcomingByPatientId(long patientId, LocalDateTime now) {
        String sql = "SELECT id, patient_id, doctor_id, start_time, status " +
                     "FROM appointments WHERE patient_id = ? AND start_time >= ? ORDER BY start_time";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, patientId);
            st.setTimestamp(2, Timestamp.valueOf(now));
            ResultSet rs = st.executeQuery();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Appointment findUpcomingByPatientId error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsBookedByDoctorAndTime(long doctorId, LocalDateTime startTime) {
        String sql = "SELECT 1 FROM appointments " +
                     "WHERE doctor_id = ? AND start_time = ? AND status = 'BOOKED' LIMIT 1";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, doctorId);
            st.setTimestamp(2, Timestamp.valueOf(startTime));
            ResultSet rs = st.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Appointment exists check error: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean cancel(long appointmentId) {
        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ? AND status = 'BOOKED'";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, appointmentId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Appointment cancel error: " + e.getMessage(), e);
        }
    }

    private Appointment map(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getLong("id"),
                rs.getLong("patient_id"),
                rs.getLong("doctor_id"),
                rs.getTimestamp("start_time").toLocalDateTime(),
                AppointmentStatus.valueOf(rs.getString("status"))
        );
    }
}
