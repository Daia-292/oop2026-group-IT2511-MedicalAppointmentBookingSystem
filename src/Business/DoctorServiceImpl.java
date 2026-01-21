package Business;

import Exceptions.InvalidInputException;
import Exceptions.NotFoundException;
import entity.Doctor;
import repositories.interfaces.IDoctorRepository;

import java.util.List;

public class DoctorServiceImpl implements DoctorService {

    private final IDoctorRepository doctorRepo;

    public DoctorServiceImpl(IDoctorRepository doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    @Override
    public int createDoctor(Doctor doctor) {
        if (doctor == null) throw new InvalidInputException("Doctor is null");
        if (isBlank(doctor.getFullName())) throw new InvalidInputException("Doctor fullName is required");
        if (isBlank(doctor.getSpecialization())) throw new InvalidInputException("Doctor specialization is required");
        return doctorRepo.create(doctor);
    }

    @Override
    public Doctor getDoctorById(int id) {
        if (id <= 0) throw new InvalidInputException("Doctor id must be positive");
        return doctorRepo.getById(id)
                .orElseThrow(() -> new NotFoundException("Doctor not found: id=" + id));
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepo.getAll();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
