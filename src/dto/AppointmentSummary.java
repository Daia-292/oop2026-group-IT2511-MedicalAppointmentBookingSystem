package dto;

import entity.*;

import java.time.LocalDateTime;

public class AppointmentSummary {

    private final long id;
    private final long patientId;
    private final long doctorId;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final AppointmentStatus status;
    private final AppointmentType type;
    private final String extra;

    private AppointmentSummary(Builder b) {
        this.id = b.id;
        this.patientId = b.patientId;
        this.doctorId = b.doctorId;
        this.start = b.start;
        this.end = b.end;
        this.status = b.status;
        this.type = b.type;
        this.extra = b.extra;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long id;
        private long patientId;
        private long doctorId;
        private LocalDateTime start;
        private LocalDateTime end;
        private AppointmentStatus status;
        private AppointmentType type;
        private String extra;

        public Builder id(long v) { id = v; return this; }
        public Builder patientId(long v) { patientId = v; return this; }
        public Builder doctorId(long v) { doctorId = v; return this; }
        public Builder start(LocalDateTime v) { start = v; return this; }
        public Builder end(LocalDateTime v) { end = v; return this; }
        public Builder status(AppointmentStatus v) { status = v; return this; }
        public Builder type(AppointmentType v) { type = v; return this; }
        public Builder extra(String v) { extra = v; return this; }

        public AppointmentSummary build() {
            return new AppointmentSummary(this);
        }
    }

    public long getId() { return id; }
    public long getPatientId() { return patientId; }
    public long getDoctorId() { return doctorId; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public AppointmentStatus getStatus() { return status; }
    public AppointmentType getType() { return type; }
    public String getExtra() { return extra; }
}
