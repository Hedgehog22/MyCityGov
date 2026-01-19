package gr.hua.dit.mycitygov.core.service.impl;


import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.model.AppointmentStatus;
import gr.hua.dit.mycitygov.core.repository.AppointmentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepository;

    public AppointmentScheduler(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void markMissedAppointmentsAsSkipped() {
        System.out.println("SCHEDULER:Checking for skipped appointments...");

        LocalDate yesterday = LocalDate.now();

        List<Appointment> staleAppointments = appointmentRepository.findAllByStatusAndSlotDateBefore(AppointmentStatus.BOOKED, yesterday);
        for (Appointment app : staleAppointments) {
            app.setStatus(AppointmentStatus.SKIPPED);
            System.out.println("MArk appointment " + app.getReferenceCode() + " as SKIPPED");
        }
        appointmentRepository.saveAll(staleAppointments);
        System.out.println("Updated " + staleAppointments.size() + " appointments to SKIPPED.");
    }
}