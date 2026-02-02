package entity;

import java.time.LocalDateTime;

public class BasicAppointment extends Appointment {
    public BasicAppointment(long id, long patientId, long doctorId,
                            LocalDateTime startTime, LocalDateTime endTime,
                            AppointmentStatus status, LocalDateTime createdAt) {
        super(id, patientId, doctorId, startTime, endTime, status, createdAt);
    }

    @Override
    public AppointmentType getType() {
        return AppointmentType.BASIC;
    }
}
