package Domain.entity;

import java.time.LocalDateTime;

public class OnlineAppointment extends Appointment {
    private final String meetingLink;

    public OnlineAppointment(long id, long patientId, long doctorId,
                             LocalDateTime startTime, LocalDateTime endTime,
                             AppointmentStatus status, LocalDateTime createdAt,
                             String meetingLink) {
        super(id, patientId, doctorId, startTime, endTime, status, createdAt);
        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalArgumentException("meetingLink cannot be blank");
        }
        this.meetingLink = meetingLink;
    }

    public AppointmentType getType() {
        return AppointmentType.ONLINE;
    }

    public String getMeetingLink() {
        return meetingLink;
    }
}
