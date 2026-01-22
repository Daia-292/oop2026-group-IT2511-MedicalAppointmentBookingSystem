package Repository;

import entity.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Doctor create(String fullName, String specialty, boolean active);
    Optional<Doctor> findById(int id);
    List<Doctor> findAllActive();
    void setActive(int doctorId, boolean active);
}
