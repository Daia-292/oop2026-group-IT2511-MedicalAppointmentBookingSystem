package Repositories;

import edu.aitu.oop3.db.IDB;
import entity.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresDoctorRepository implements repository.DoctorRepository {

    private final IDB db;

    public PostgresDoctorRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Doctor save(Doctor doctor) {
        String sql = "INSERT INTO doctors(full_name, specialty, active) VALUES (?, ?, ?) RETURNING id";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, doctor.getFullName());
            st.setString(2, doctor.getSpecialty());
            st.setBoolean(3, doctor.isActive());

            ResultSet rs = st.executeQuery();
            if (rs.next()) doctor.setId(rs.getLong("id"));
            return doctor;
        } catch (SQLException e) {
            throw new RuntimeException("Doctor save error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Doctor> findById(long id) {
        String sql = "SELECT id, full_name, specialty, active FROM doctors WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return Optional.of(new Doctor(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("specialty"),
                        rs.getBoolean("active")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Doctor findById error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Doctor> findAll() {
        String sql = "SELECT id, full_name, specialty, active FROM doctors ORDER BY id";
        List<Doctor> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("specialty"),
                        rs.getBoolean("active")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Doctor findAll error: " + e.getMessage(), e);
        }
    }
}
