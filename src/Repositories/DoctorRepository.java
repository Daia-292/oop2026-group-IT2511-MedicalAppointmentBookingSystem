package repository;

import entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor save(Doctor doctor);
    Optional<Doctor> findById(long id);
    List<Doctor> findAll();
}
