package entity;
import java.time.LocalDateTime;

public class Appointment {
    private final long id;
    private final long patientId;
    private final long doctorId;
    private final LocalDateTime startTime;
    private final AppointmentStatus status;

    public Appointment(long id, long patientId, long doctorId, LocalDateTime startTime, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.status = status;
    }

    public long getId() { return id; }
    public long getPatientId() { return patientId; }
    public long getDoctorId() { return doctorId; }
    public LocalDateTime getStartTime() { return startTime; }
    public AppointmentStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "Appointment{id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId +
                ", startTime=" + startTime + ", status=" + status + "}";
    }
}
