package repository;

import edu.aitu.oop3.db.IDB;
import entity.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostgresPatientRepository implements PatientRepository {

    private final IDB db;

    public PostgresPatientRepository(IDB db) {
        this.db = db;
    }

    @Override
    public Patient save(Patient patient) {
        String sql = "INSERT INTO patients(full_name, phone) VALUES (?, ?) RETURNING id";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setString(1, patient.getFullName());
            st.setString(2, patient.getPhone());

            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                patient.setId(rs.getLong("id"));
            }
            return patient;
        } catch (SQLException e) {
            throw new RuntimeException("Patient save error: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Patient> findById(long id) {
        String sql = "SELECT id, full_name, phone FROM patients WHERE id = ?";
        try (Connection con = db.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return Optional.of(new Patient(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Patient findById error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT id, full_name, phone FROM patients ORDER BY id";
        List<Patient> list = new ArrayList<>();
        try (Connection con = db.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Patient(
                        rs.getLong("id"),
                        rs.getString("full_name"),
                        rs.getString("phone")
                ));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Patient findAll error: " + e.getMessage(), e);
        }
    }
}
