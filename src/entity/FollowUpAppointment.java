package entity;

import java.time.LocalDateTime;

public class FollowUpAppointment extends Appointment {
    private final long previousAppointmentId;
    private final String note;

    public FollowUpAppointment(long id, long patientId, long doctorId,
                               LocalDateTime startTime, LocalDateTime endTime,
                               AppointmentStatus status, LocalDateTime createdAt,
                               long previousAppointmentId, String note) {
        super(id, patientId, doctorId, startTime, endTime, status, createdAt);
        if (previousAppointmentId <= 0) {
            throw new IllegalArgumentException("previousAppointmentId must be > 0");
        }
        this.previousAppointmentId = previousAppointmentId;
        this.note = (note == null) ? "" : note;
    }

    public AppointmentType getType() {
        return AppointmentType.FOLLOW_UP;
    }

    public long getPreviousAppointmentId() {
        return previousAppointmentId;
    }

    public String getNote() {
        return note;
    }
}
