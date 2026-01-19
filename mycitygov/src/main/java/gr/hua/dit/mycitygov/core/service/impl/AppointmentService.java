package gr.hua.dit.mycitygov.core.service.impl;
import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.port.HolidayProviderPort;
import gr.hua.dit.mycitygov.core.port.NotificationProviderPort;
import gr.hua.dit.mycitygov.core.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {

    private final AppointmentSlotRepository slotRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    private final HolidayProviderPort holidayProvider;
    private final NotificationProviderPort notificationProvider;

    public AppointmentService(AppointmentSlotRepository slotRepository,AppointmentRepository appointmentRepository,
                              UserRepository userRepository, HolidayProviderPort holidayProvider, NotificationProviderPort notificationProvider) {
        this.slotRepository = slotRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.holidayProvider = holidayProvider;
        this.notificationProvider = notificationProvider;
    }

    public List<LocalDate> getAvailableDates(Long deptId) {
        return slotRepository.findDistinctAvailableDates(deptId);
    }

    public List<AppointmentSlot> getSlotsForDate(Long deptId, LocalDate date) {
        return slotRepository.findAvailableSlotsByDepartmentAndDate(deptId, date);
    }
    @Transactional
    public void generateSlotsForRange(Long employeeId, LocalDate startDate, LocalDate endDate,
                                      LocalTime startTime, LocalTime endTime,
                                      boolean skipWeekends,
                                      LocalTime breakStart,
                                      LocalTime breakEnd) {

        User employee = userRepository.findById(employeeId).orElseThrow();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (holidayProvider.isHoliday(currentDate)) {
                System.out.println("Skipping holiday: " + currentDate);
                currentDate = currentDate.plusDays(1);
                continue;
            }
            if (skipWeekends) {
                DayOfWeek day = currentDate.getDayOfWeek();
                if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }
            }

            generateDailySlotsInternal(employee, currentDate, startTime, endTime, breakStart, breakEnd);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void generateDailySlotsInternal(User employee, LocalDate date,
                                            LocalTime start, LocalTime end,
                                            LocalTime breakStart, LocalTime breakEnd) {
        LocalTime currentSlotStart = start;

        while (currentSlotStart.plusMinutes(15).isBefore(end) ||  currentSlotStart.plusMinutes(15).equals(end)) {
            LocalTime currentSlotEnd = currentSlotStart.plusMinutes(15);
            boolean isLunchTime = false;
            if (breakStart != null && breakEnd != null) {
                if ((currentSlotStart.isAfter(breakStart) || currentSlotStart.equals(breakStart)) &&
                        currentSlotStart.isBefore(breakEnd)) {
                    isLunchTime = true;
                }
            }
            if (!isLunchTime) {
                if (!slotRepository.existsByEmployeeIdAndDateAndStartTime(employee.getId(), date, currentSlotStart)) {
                    AppointmentSlot slot = new AppointmentSlot(date, currentSlotStart, currentSlotEnd, employee);
                    slotRepository.save(slot);
                }
            }
            currentSlotStart = currentSlotStart.plusMinutes(15);
        }
    }

    @Transactional
    public void deleteSlotsForDay(Long employeeId, LocalDate date) {
        List<AppointmentSlot> slots = slotRepository.findAllByEmployeeIdAndDate(employeeId, date); // Цей метод треба додати в репозиторій

        for (AppointmentSlot slot : slots) {
            if (!slot.getIsAvailable()) {
                throw new RuntimeException("Unable to delete, slot is booked " + slot.getStartTime());
            }
        }
        slotRepository.deleteAll(slots);
    }
    @Transactional
    public void bookAppointment(Long slotId, String userEmail) {
        AppointmentSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));
        if (holidayProvider.isHoliday(slot.getDate())) {
            throw new RuntimeException("Cannot book an appointment on a Holiday!");
        }

        if (!slot.getIsAvailable()) {
            throw new RuntimeException("Slot is already booked or unavailable!");
        }

        User citizen = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Appointment appointment = new Appointment();
        appointment.setSlot(slot);
        appointment.setCitizen(citizen);

        appointment.setStatus(AppointmentStatus.BOOKED);

        appointment.setReferenceCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        slot.setIsAvailable(false);

        slotRepository.save(slot);
        appointmentRepository.save(appointment);

        String rawPhone = citizen.getPhoneNumber();
        String formattedPhone = rawPhone;

        if (!rawPhone.isEmpty()) {
            if (!rawPhone.startsWith("+")) {
                formattedPhone = "+30" + rawPhone;
            }
            String msg = "Your appointment is confirmed! Ref: " + appointment.getReferenceCode();
            notificationProvider.sendNotification(formattedPhone, msg);
        } else {
            System.out.println("⚠Warning: SMS verification failed, but appointment is booked.");
        }
    }

    @Transactional
    public void completeAppointment(Long appointmentId) {
        Appointment app = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        LocalDate date = app.getSlot().getDate();
        LocalTime time = app.getSlot().getStartTime();
        LocalDateTime appointmentStart = LocalDateTime.of(date, time);

        if (LocalDateTime.now().isBefore(appointmentStart)) {
            throw new RuntimeException("PLease, repeat after deadline of appointment!");
        }
        app.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(app);
    }


    @Transactional
    public void cancelAppointmentByCitizen(Long appointmentId, String citizenEmail) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);

        if (!appointment.getCitizen().getEmail().equals(citizenEmail)) {
            throw new RuntimeException("Access Denied: You can only cancel your own appointments.");
        }

        performCancellation(appointment);
    }

    @Transactional
    public void cancelAppointmentByEmployee(Long appointmentId) {
        Appointment appointment = getAppointmentOrThrow(appointmentId);
        performCancellation(appointment);
    }

    private void performCancellation(Appointment appointment) {
        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new RuntimeException("Cannot cancel appointment with status: " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);

        AppointmentSlot slot = appointment.getSlot();
        slot.setIsAvailable(true);

        slotRepository.save(slot);
        appointmentRepository.save(appointment);

        //TO DO announcement
    }

    private Appointment getAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public List<Appointment> getMyAppointments(String citizenEmail) {
        User citizen = userRepository.findByEmail(citizenEmail).orElseThrow();
        return appointmentRepository.findByCitizenId(citizen.getId());
    }
}