package Business;

import Exceptions.DoctorUnavailableException;
import Exceptions.NotFoundException;
import repository.AppointmentRepository;
import repository.DoctorRepository;

import java.time.LocalDateTime;

public class DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorAvailabilityService(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public void ensureDoctorIsAvailable(long doctorId, LocalDateTime startTime) {
        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Doctor not found: id=" + doctorId));

        if (!doctor.isActive()) {
            throw new DoctorUnavailableException("Doctor is not active сейчас: id=" + doctorId);
        }

        boolean slotTaken = appointmentRepository.existsBookedByDoctorAndTime(doctorId, startTime);
        if (slotTaken) {
            throw new DoctorUnavailableException("Time slot already booked for doctorId=" + doctorId + " at " + startTime);
        }
    }
}

