package Business;
import entity.Patient;
import java.util.List;

public interface PatientService {
    int createPatient(Patient patient);
    Patient getPatientById(int id);
    List<Patient> getAllPatients();
}
