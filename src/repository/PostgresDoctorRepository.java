package repository;

import edu.aitu.oop3.db.DatabaseConnection;
import entity.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresDoctorRepository implements DoctorRepository {

    private final DatabaseConnection db;

    public PostgresDoctorRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Doctor create(String fullName, String specialty, boolean active) {
        String sql = "INSERT INTO doctors (full_name, specialty, active) VALUES (?, ?, ?) RETURNING id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, specialty);
            ps.setBoolean(3, active);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(rs.getInt("id"), fullName, specialty, active);
                }
            }
            throw new RuntimeException("Cannot add doctor");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Doctor> findById(int id) {
        String sql = "SELECT id, full_name, specialty, active FROM doctors WHERE id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Doctor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("specialty"),
                            rs.getBoolean("active")
                    ));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Doctor> findAllActive() {
        String sql = "SELECT id, full_name, specialty, active FROM doctors WHERE active = true ORDER BY id";
        List<Doctor> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialty"),
                        rs.getBoolean("active")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setActive(int doctorId, boolean active) {
        String sql = "UPDATE doctors SET active = ? WHERE id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, active);
            ps.setInt(2, doctorId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

