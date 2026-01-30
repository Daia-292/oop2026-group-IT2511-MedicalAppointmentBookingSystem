package factory;

import entity.*;

import java.time.LocalDateTime;

public class AppointmentFactory {

    public Appointment create(AppointmentType type,
                              long patientId,
                              long doctorId,
                              LocalDateTime start,
                              LocalDateTime end,
                              String meetingLink,
                              String roomNumber,
                              Long prevId) {

        return switch (type) {
            case ONLINE -> new OnlineAppointment(
                    patientId, doctorId, start, end,
                    AppointmentStatus.BOOKED, meetingLink
            );
            case IN_PERSON -> new InPersonAppointment(
                    patientId, doctorId, start, end,
                    AppointmentStatus.BOOKED, roomNumber
            );
            case FOLLOW_UP -> new FollowUpAppointment(
                    patientId, doctorId, start, end,
                    AppointmentStatus.BOOKED,
                    prevId == null ? 0 : prevId
            );
        };
    }
}
