package entity;

import java.time.LocalDateTime;

public class Appointment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime startTime;
    private AppointmentStatus status;

    public Appointment() {}

    public Appointment(Long id, Long patientId, Long doctorId, LocalDateTime startTime, AppointmentStatus status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.startTime = startTime;
        this.status = status;
    }

    public Appointment(Long patientId, Long doctorId, LocalDateTime startTime) {
        this(null, patientId, doctorId, startTime, AppointmentStatus.BOOKED);
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public Long getDoctorId() { return doctorId; }
    public LocalDateTime getStartTime() { return startTime; }
    public AppointmentStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Appointment{id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId +
                ", startTime=" + startTime + ", status=" + status + "}";
    }
}
