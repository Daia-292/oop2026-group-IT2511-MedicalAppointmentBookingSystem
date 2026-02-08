package Domain.repository;

import Domain.entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import Reporting.dto.Page;


public interface AppointmentRepository {
    Appointment createBooked(int patientId, int doctorId, LocalDateTime startAt, LocalDateTime endAt);

    Appointment create(Appointment appt);

    Optional<Appointment> findById(int id);

    List<Appointment> findByDoctor(int doctorId);
    List<Appointment> findUpcomingByPatient(int patientId);
    Page<Appointment> findByDoctorPaged(int doctorId, int page, int size);

    boolean hasOverlapBooked(int doctorId, LocalDateTime startAt, LocalDateTime endAt);

    void cancel(int appointmentId);
}
