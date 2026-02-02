package Business;

import Exceptions.AppointmentNotFoundException;
import Exceptions.DoctorUnavailableException;
import Exceptions.TimeSlotAlreadyBookedException;
import repository.AppointmentRepository;

import entity.Appointment;
import entity.AppointmentStatus;
import entity.FollowUpAppointment;
import entity.InPersonAppointment;
import entity.OnlineAppointment;

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

    public void cancel(int id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found: " + id));

        if (appt.getStatus() != AppointmentStatus.BOOKED) {
            throw new IllegalStateException("Only BOOKED appointment can be canceled");
        }

        repo.cancel(id);
    }

    public Appointment bookOnline(int patientId, int doctorId,
                                  LocalDateTime startAt, LocalDateTime endAt,
                                  String meetingLink) {
        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalArgumentException("meetingLink must not be blank");
        }

        Appointment appt = new OnlineAppointment(
                0, patientId, doctorId,
                startAt, endAt,
                AppointmentStatus.BOOKED,
                LocalDateTime.now(),
                meetingLink
        );

        // NOTE: if repo.create(appt) doesn't exist yet, add it to AppointmentRepository
        // and implement in PostgresAppointmentRepository, or replace with an existing repo method.
        return repo.create(appt);
    }

    public Appointment bookInPerson(int patientId, int doctorId,
                                    LocalDateTime startAt, LocalDateTime endAt,
                                    String room) {
        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException("room must not be blank");
        }

        Appointment appt = new InPersonAppointment(
                0, patientId, doctorId,
                startAt, endAt,
                AppointmentStatus.BOOKED,
                LocalDateTime.now(),
                room
        );
        return repo.create(appt);
    }

    public Appointment bookFollowUp(int patientId, int doctorId,
                                    LocalDateTime startAt, LocalDateTime endAt,
                                    int previousAppointmentId, String note) {
        if (previousAppointmentId <= 0) {
            throw new IllegalArgumentException("previousAppointmentId must be positive");
        }

        Appointment appt = new FollowUpAppointment(
                0, patientId, doctorId,
                startAt, endAt,
                AppointmentStatus.BOOKED,
                LocalDateTime.now(),
                previousAppointmentId,
                note
        );
        return repo.create(appt);
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
