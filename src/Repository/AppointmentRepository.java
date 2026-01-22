package Repository;

import entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {
    Appointment createBooked(int patientId, int doctorId, LocalDateTime startAt, LocalDateTime endAt);
    Optional<Appointment> findById(int id);

    List<Appointment> findByDoctor(int doctorId);
    List<Appointment> findUpcomingByPatient(int patientId);

    boolean hasOverlapBooked(int doctorId, LocalDateTime startAt, LocalDateTime endAt);

    void cancel(int appointmentId);
}
