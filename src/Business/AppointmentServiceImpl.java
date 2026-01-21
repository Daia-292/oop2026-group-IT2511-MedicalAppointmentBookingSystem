package Business;

import Exceptions.ConflictException;
import Exceptions.InvalidInputException;
import Exceptions.NotFoundException;
import entity.Appointment;
import entity.AppointmentStatus;
import repositories.interfaces.IAppointmentRepository;
import repositories.interfaces.IDoctorRepository;
import repositories.interfaces.IPatientRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {

    private final IAppointmentRepository appointmentRepo;
    private final IPatientRepository patientRepo;
    private final IDoctorRepository doctorRepo;
    private final DoctorAvailabilityService availabilityService;

    public AppointmentServiceImpl(
            IAppointmentRepository appointmentRepo,
            IPatientRepository patientRepo,
            IDoctorRepository doctorRepo,
            DoctorAvailabilityService availabilityService
    ) {
        this.appointmentRepo = appointmentRepo;
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.availabilityService = availabilityService;
    }

    @Override
    public int bookAppointment(int patientId, int doctorId, LocalDateTime startTime) {
        if (patientId <= 0) throw new InvalidInputException("Patient id must be positive");
        if (doctorId <= 0) throw new InvalidInputException("Doctor id must be positive");
        if (startTime == null) throw new InvalidInputException("startTime is null");

        // 1) Проверяем существование сущностей
        patientRepo.getById(patientId).orElseThrow(() ->
                new NotFoundException("Patient not found: id=" + patientId));

        doctorRepo.getById(doctorId).orElseThrow(() ->
                new NotFoundException("Doctor not found: id=" + doctorId));

        // 2) Проверяем доступность врача/слота
        if (!availabilityService.isDoctorAvailable(doctorId, startTime)) {
            throw new ConflictException("Time slot already booked or doctor unavailable: doctorId="
                    + doctorId + ", startTime=" + startTime);
        }

        // 3) Создаём запись
        Appointment appt = new Appointment();
        appt.setPatientId(patientId);
        appt.setDoctorId(doctorId);
        appt.setStartTime(startTime);
        appt.setStatus(AppointmentStatus.BOOKED);

        return appointmentRepo.create(appt);
    }

    @Override
    public void cancelAppointment(int appointmentId) {
        if (appointmentId <= 0) throw new InvalidInputException("Appointment id must be positive");

        Appointment appt = appointmentRepo.getById(appointmentId).orElseThrow(() ->
                new NotFoundException("Appointment not found: id=" + appointmentId));

        if (appt.getStatus() == AppointmentStatus.CANCELLED) {
            // можно не считать ошибкой, но как конфликт — тоже ок
            throw new ConflictException("Appointment already cancelled: id=" + appointmentId);
        }

        appt.setStatus(AppointmentStatus.CANCELLED);
        boolean updated = appointmentRepo.updateStatus(appointmentId, AppointmentStatus.CANCELLED);

        if (!updated) {
            throw new ConflictException("Cancel failed (concurrent update?): id=" + appointmentId);
        }
    }

    @Override
    public Appointment getAppointmentById(int id) {
        if (id <= 0) throw new InvalidInputException("Appointment id must be positive");
        return appointmentRepo.getById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found: id=" + id));
    }

    @Override
    public List<Appointment> getDoctorSchedule(int doctorId) {
        if (doctorId <= 0) throw new InvalidInputException("Doctor id must be positive");
        // если хочешь — проверь doctorRepo.getById(doctorId) на NotFound
        return appointmentRepo.getByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> getPatientUpcomingAppointments(int patientId) {
        if (patientId <= 0) throw new InvalidInputException("Patient id must be positive");
        return appointmentRepo.getUpcomingByPatientId(patientId, LocalDateTime.now());
    }
}
