package Business;

import Exceptions.AppointmentNotFoundException;
import Exceptions.DoctorUnavailableException;
import Exceptions.TimeSlotAlreadyBookedException;
import repository.AppointmentRepository;

import entity.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }

    public Appointment book(int patientId, int doctorId, LocalDateTime startAt, LocalDateTime endAt) {
        if (patientId <= 0 || doctorId <= 0) {
            throw new IllegalArgumentException("patientId/doctorId must be positive");
        }
        if (startAt == null || endAt == null || !endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("Invalid time range");
        }
        if (startAt.isBefore(LocalDateTime.now())) {
            throw new DoctorUnavailableException("Cannot book appointment in the past");
        }

        boolean overlap = repo.hasOverlapBooked(doctorId, startAt, endAt);
        if (overlap) {
            throw new TimeSlotAlreadyBookedException("This time slot is already booked");
        }

        return repo.createBooked(patientId, doctorId, startAt, endAt);
    }

    // user flow: cancel appointment
    public void cancel(int id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));

        if (!"BOOKED".equals(appt.getStatus())) {
            throw new IllegalStateException("Only BOOKED appointment can be canceled");
        }

        repo.cancel(id);
    }

    // user flow: view doctor's schedule
    public List<Appointment> doctorSchedule(int doctorId) {
        return repo.findByDoctor(doctorId);
    }

    // user flow: view patient's upcoming visits
    public List<Appointment> patientUpcoming(int patientId) {
        return repo.findUpcomingByPatient(patientId);
    }
}
