package repository;

import entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(long id);
    List<Appointment> findByDoctorId(long doctorId);
    List<Appointment> findUpcomingByPatientId(long patientId, LocalDateTime now);
    boolean existsBookedByDoctorAndTime(long doctorId, LocalDateTime startTime);
    boolean cancel(long appointmentId);
}
