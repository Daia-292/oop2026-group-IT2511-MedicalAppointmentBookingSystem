package repository;

import entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(long id);
    List<Patient> findAll();
}
