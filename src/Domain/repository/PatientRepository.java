package Domain.repository;

import Domain.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient create(String fullName, String phone);
    Optional<Patient> findById(int id);
    List<Patient> findAll();
}
