package Repository;

import edu.aitu.oop3.db.DatabaseConnection;
import entity.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresPatientRepository implements PatientRepository {

    private final DatabaseConnection db;

    public PostgresPatientRepository(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public Patient create(String fullName, String phone) {
        String sql = "INSERT INTO patients (full_name, phone) VALUES (?, ?) RETURNING id";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, phone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Patient(rs.getInt("id"), fullName, phone);
                }
            }
            throw new RuntimeException("Не удалось создать patient");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Patient> findById(int id) {
        String sql = "SELECT id, full_name, phone FROM patients WHERE id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Patient(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("phone")
                    ));
                }
            }
            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT id, full_name, phone FROM patients ORDER BY id";
        List<Patient> list = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Patient(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                ));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

