package Business;

import entity.Appointment;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    int bookAppointment(int patientId, int doctorId, LocalDateTime startTime);
    void cancelAppointment(int appointmentId);
    Appointment getAppointmentById(int id);

    List<Appointment> getDoctorSchedule(int doctorId);
    List<Appointment> getPatientUpcomingAppointments(int patientId);
}
