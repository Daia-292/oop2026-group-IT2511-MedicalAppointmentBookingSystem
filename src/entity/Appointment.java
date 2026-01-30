package entity;
import java.time.LocalDateTime;

public class Appointment {
    private final long id;
    private final long patientId;
    private final long doctorId;
    private final LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private AppointmentStatus status;

    public Appointment(long id, long patientId, long doctorId, LocalDateTime startTime,LocalDateTime endTime,
                       AppointmentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }




    public long getId() { return id; }
    public long getPatientId() { return patientId; }
    public long getDoctorId() { return doctorId; }
    public LocalDateTime getStartTime() { return startTime; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getEndTime() { return endTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setStatus(AppointmentStatus status) { this.status = status; }



    @Override
    public String toString() {
        return "Appointment id=" + id + ", patient Id=" + patientId + ", doctor Id=" + doctorId +
                ", startTime=" + startTime + ", status=" + status + "}";
    }
}
