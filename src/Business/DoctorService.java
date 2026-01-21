package Business;

import entity.Doctor;
import java.util.List;

public interface DoctorService {
    int createDoctor(Doctor doctor);
    Doctor getDoctorById(int id);
    List<Doctor> getAllDoctors();
}
