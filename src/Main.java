
package ui;

import Business.AppointmentService;
import Business.DoctorAvailabilityService;
import Exceptions.*;
import edu.aitu.oop3.db.IDB;
import edu.aitu.oop3.db.PostgresDB;
import entity.Doctor;
import entity.Patient;
import repository.*;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        IDB db = new PostgresDB();

        PatientRepository patientRepo = new PostgresPatientRepository(db);
        DoctorRepository doctorRepo = new PostgresDoctorRepository(db);
        AppointmentRepository appointmentRepo = new PostgresAppointmentRepository(db);

        DoctorAvailabilityService availabilityService = new DoctorAvailabilityService(doctorRepo, appointmentRepo);
        AppointmentService appointmentService = new AppointmentService(appointmentRepo, patientRepo, availabilityService);

        try {

            Patient p = patientRepo.save(new Patient("Ayan Sapar", "+7 777 111 22 33"));
            Doctor d = doctorRepo.save(new Doctor("Dr. Aliya", "Therapist", true));


            LocalDateTime slot = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
            var appt = appointmentService.book(p.getId(), d.getId(), slot);
            System.out.println("BOOKED: " + appt);

            System.out.println("Doctor schedule: " + appointmentService.viewDoctorSchedule(d.getId()));

            System.out.println("Patient upcoming: " + appointmentService.viewUpcomingForPatient(p.getId()));

            appointmentService.cancel(appt.getId());
            System.out.println("Cancelled appointment id=" + appt.getId());

        } catch (InvalidInputException | NotFoundException | ConflictException | DoctorUnavailableException e) {
            System.out.println("Business error: " + e.getMessage());
        } catch (RuntimeException e) {
            // JDBC немесе басқа күтпеген error
            System.out.println("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
