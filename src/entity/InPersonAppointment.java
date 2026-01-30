package entity;

import java.time.LocalDateTime;

public class InPersonAppointment extends Appointment {
    private final String room;

    public InPersonAppointment(long id, long patientId, long doctorId,
                               LocalDateTime startTime, LocalDateTime endTime,
                               AppointmentStatus status, LocalDateTime createdAt,
                               String room) {
        super(id, patientId, doctorId, startTime, endTime, status, createdAt);
        if (room == null || room.isBlank()) {
            throw new IllegalArgumentException("room cannot be blank");
        }
        this.room = room;
    }

    public AppointmentType getType() {
        return AppointmentType.IN_PERSON;
    }

    public String getRoom() {
        return room;
    }
}
