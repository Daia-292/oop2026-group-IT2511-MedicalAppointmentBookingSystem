package Business;

import Exceptions.InvalidInputException;
import repositories.interfaces.IAppointmentRepository;

import java.time.LocalDateTime;

public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    private final IAppointmentRepository appointmentRepo;

    public DoctorAvailabilityServiceImpl(IAppointmentRepository appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    @Override
    public boolean isDoctorAvailable(int doctorId, LocalDateTime startTime) {
        if (doctorId <= 0) throw new InvalidInputException("Doctor id must be positive");
        if (startTime == null) throw new InvalidInputException("startTime is null");

        // Репозиторий должен проверить пересечение слотов:
        // SELECT exists( ... where doctor_id=? and start_time=? and status='BOOKED')
        return !appointmentRepo.existsBookedSlot(doctorId, startTime);
    }
}
