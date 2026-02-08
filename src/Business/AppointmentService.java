package Business;

import Domain.factory.AppointmentFactory;
import Reporting.dto.AppointmentSummary;
import Business.Exceptions.AppointmentNotFoundException;
import Business.Exceptions.DoctorUnavailableException;
import Business.Exceptions.TimeSlotAlreadyBookedException;
import Domain.config.WorkingHoursProvider;
import Reporting.dto.Result;
import Domain.repository.AppointmentRepository;
import Domain.entity.Appointment;
import Domain.entity.AppointmentStatus;
import Domain.entity.FollowUpAppointment;
import Domain.entity.InPersonAppointment;
import Domain.entity.OnlineAppointment;
import Reporting.dto.Page;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {

    private final AppointmentRepository repo;
    private final WorkingHoursProvider workingHours;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
        this.workingHours = WorkingHoursProvider.getInstance();
    }

    public AppointmentSummary toSummary(Appointment a) {
        return AppointmentSummary.builder()
                .patientId(a.getPatientId())
                .doctorId(a.getDoctorId())
                .startTime(a.getStartTime())
                .endTime(a.getEndTime())
                .status(a.getStatus())
                .type(a.getType())
                .extra(extractExtra(a))
                .build();
    }
    private String extractExtra(Appointment a) {
        if (a instanceof OnlineAppointment oa) return oa.getMeetingLink();
        if (a instanceof InPersonAppointment ip) return ip.getRoom();
        if (a instanceof FollowUpAppointment fu) return fu.getNote();
        return "";
    }

    public Page<Appointment> doctorSchedulePaged(int doctorId, int page, int size) {
        return repo.findByDoctorPaged(doctorId, page, size);
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
        boolean withinHours = workingHours.isWithin(
                startAt.toLocalTime(),
                endAt.toLocalTime()
        );

        if (!withinHours) {
            throw new DoctorUnavailableException(
                    "Appointment must be within clinic working hours (09:00–18:00)"
            );
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

        book(patientId, doctorId, startAt, endAt);

        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalArgumentException("meetingLink must not be blank");
        }

        if (repo.hasOverlapBooked(doctorId, startAt, endAt)) {
            throw new TimeSlotAlreadyBookedException("This time slot is already booked");
        }

        Appointment appt = AppointmentFactory.newOnline(patientId, doctorId, startAt, endAt, meetingLink);
        return repo.create(appt);
    }


    public Appointment bookInPerson(int patientId, int doctorId,
                                    LocalDateTime startAt, LocalDateTime endAt,
                                    String room) {

        book(patientId, doctorId, startAt, endAt);

        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException("room must not be blank");
        }

        if (repo.hasOverlapBooked(doctorId, startAt, endAt)) {
            throw new TimeSlotAlreadyBookedException("This time slot is already booked");
        }

        Appointment appt = AppointmentFactory.newInPerson(
                patientId, doctorId, startAt, endAt, room
        );

        return repo.create(appt);
    }


    public Appointment bookFollowUp(int patientId, int doctorId,
                                    LocalDateTime startAt, LocalDateTime endAt,
                                    int previousAppointmentId,
                                    String note) {

        book(patientId, doctorId, startAt, endAt);

        if (previousAppointmentId <= 0) {
            throw new IllegalArgumentException("previousAppointmentId must be positive");
        }

        if (repo.hasOverlapBooked(doctorId, startAt, endAt)) {
            throw new TimeSlotAlreadyBookedException("This time slot is already booked");
        }

        Appointment appt = AppointmentFactory.newFollowUp(
                patientId, doctorId, startAt, endAt, previousAppointmentId, note
        );

        return repo.create(appt);
    }

    public Result<Appointment> bookSafe(int patientId, int doctorId,
                                        LocalDateTime startAt, LocalDateTime endAt) {
        try {
            return Result.ok(book(patientId, doctorId, startAt, endAt));
        } catch (RuntimeException ex) {
            return Result.fail(ex.getMessage());
        }

    }
    public List<Appointment> doctorSchedule(int doctorId) {
        return repo.findByDoctor(doctorId);
    }

    public List<Appointment> patientUpcoming(int patientId) {
        return repo.findUpcomingByPatient(patientId);
    }
}
