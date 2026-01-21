package Business;

import Exceptions.InvalidInputException;
import Exceptions.NotFoundException;
import entity.Patient;
import repositories.interfaces.IPatientRepository;

import java.util.List;

public class PatientServiceImpl implements PatientService {

    private final IPatientRepository patientRepo;

    public PatientServiceImpl(IPatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    @Override
    public int createPatient(Patient patient) {
        if (patient == null) {
            throw new InvalidInputException("Patient is null");
        }
        if (isBlank(patient.getFullName())) {
            throw new InvalidInputException("Patient fullName is required");
        }
        if (isBlank(patient.getPhone())) {
            throw new InvalidInputException("Patient phone is required");
        }
        return patientRepo.create(patient);
    }

    @Override
    public Patient getPatientById(int id) {
        if (id <= 0) throw new InvalidInputException("Patient id must be positive");
        return patientRepo.getById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found: id=" + id));
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepo.getAll();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
