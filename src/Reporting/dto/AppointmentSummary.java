package Reporting.dto;

import Domain.entity.AppointmentStatus;
import Domain.entity.AppointmentType;

import java.time.LocalDateTime;

public class AppointmentSummary {
    private final long appointmentId;
    private final AppointmentType type;
    private final long patientId;
    private final long doctorId;

    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final AppointmentStatus status;
    private final LocalDateTime createdAt;

    private final String meetingLink;
    private final String room;
    private final Long previousAppointmentId;
    private final String note;

    private AppointmentSummary(Builder b) {
        this.appointmentId = b.appointmentId;
        this.type = b.type;
        this.patientId = b.patientId;
        this.doctorId = b.doctorId;
        this.startTime = b.startTime;
        this.endTime = b.endTime;
        this.status = b.status;
        this.createdAt = b.createdAt;
        this.meetingLink = b.meetingLink;
        this.room = b.room;
        this.previousAppointmentId = b.previousAppointmentId;
        this.note = b.note;
    }

    public long getAppointmentId() { return appointmentId; }
    public AppointmentType getType() { return type; }
    public long getPatientId() { return patientId; }
    public long getDoctorId() { return doctorId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getMeetingLink() { return meetingLink; }
    public String getRoom() { return room; }
    public Long getPreviousAppointmentId() { return previousAppointmentId; }
    public String getNote() { return note; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long appointmentId;
        private AppointmentType type;
        private long patientId;
        private long doctorId;

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private AppointmentStatus status;
        private LocalDateTime createdAt;

        private String meetingLink;
        private String room;
        private Long previousAppointmentId;
        private String note;

        public Builder appointmentId(long id) { this.appointmentId = id; return this; }
        public Builder type(AppointmentType type) { this.type = type; return this; }
        public Builder patientId(long id) { this.patientId = id; return this; }
        public Builder doctorId(long id) { this.doctorId = id; return this; }

        public Builder startTime(LocalDateTime t) { this.startTime = t; return this; }
        public Builder endTime(LocalDateTime t) { this.endTime = t; return this; }
        public Builder status(AppointmentStatus s) { this.status = s; return this; }
        public Builder createdAt(LocalDateTime t) { this.createdAt = t; return this; }

        public Builder meetingLink(String link) { this.meetingLink = link; return this; }
        public Builder room(String room) { this.room = room; return this; }
        public Builder previousAppointmentId(Long id) { this.previousAppointmentId = id; return this; }
        public Builder note(String note) { this.note = note; return this; }

        public AppointmentSummary build() {
            if (type == null) throw new IllegalStateException("type is required");
            if (startTime == null) throw new IllegalStateException("startTime is required");
            if (status == null) throw new IllegalStateException("status is required");
            if (createdAt == null) throw new IllegalStateException("createdAt is required");

            switch (type) {
                case ONLINE -> {
                    if (meetingLink == null || meetingLink.isBlank())
                        throw new IllegalStateException("meetingLink required for ONLINE");
                }
                case IN_PERSON -> {
                    if (room == null || room.isBlank())
                        throw new IllegalStateException("room required for IN_PERSON");
                }
                case FOLLOW_UP -> {
                    if (previousAppointmentId == null || previousAppointmentId <= 0)
                        throw new IllegalStateException("previousAppointmentId required for FOLLOW_UP");
                }
            }
            return new AppointmentSummary(this);
        }

        public Builder extra(String s) {
            return this;
        }
    }
}
