package Domain.factory;

import Domain.entity.*;

import java.time.LocalDateTime;

public final class AppointmentFactory {
    private AppointmentFactory() {}

    private static long newId() { return 0L; }

    public static OnlineAppointment newOnline(long patientId, long doctorId,
                                              LocalDateTime startTime, LocalDateTime endTime,
                                              String meetingLink) {
        return new OnlineAppointment(
                newId(), patientId, doctorId,
                startTime, endTime,
                AppointmentStatus.BOOKED, LocalDateTime.now(),
                meetingLink
        );
    }

    public static InPersonAppointment newInPerson(long patientId, long doctorId,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  String room) {
        return new InPersonAppointment(
                newId(), patientId, doctorId,
                startTime, endTime,
                AppointmentStatus.BOOKED, LocalDateTime.now(),
                room
        );
    }

    public static FollowUpAppointment newFollowUp(long patientId, long doctorId,
                                                  LocalDateTime startTime, LocalDateTime endTime,
                                                  long previousAppointmentId,
                                                  String note) {
        return new FollowUpAppointment(
                newId(), patientId, doctorId,
                startTime, endTime,
                AppointmentStatus.BOOKED, LocalDateTime.now(),
                previousAppointmentId, note
        );
    }
}
