package gr.hua.dit.mycitygov.web.ui;
import gr.hua.dit.mycitygov.core.model.Appointment;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import gr.hua.dit.mycitygov.core.service.impl.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/citizen/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DepartmentRepository departmentRepository;

    public AppointmentController(AppointmentService appointmentService, DepartmentRepository departmentRepository) {
        this.appointmentService = appointmentService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public String showStep1(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        return "citizen/appointments/step1";
    }

    @GetMapping("/dates")
    public String showDates(@RequestParam("deptId") Long deptId, Model model) {
        List<LocalDate> availableDates = appointmentService.getAvailableDates(deptId);

        model.addAttribute("dates", availableDates);
        model.addAttribute("deptId", deptId);

        departmentRepository.findById(deptId).ifPresent(d -> model.addAttribute("deptName", d.getName()));

        return "citizen/appointments/dates";
    }

    @GetMapping("/slots")
    public String showSlots(@RequestParam("deptId") Long deptId,
                            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                            Model model) {

        List<LocalTime> availableTimes = appointmentService.getAvailableHours(deptId, date);

        model.addAttribute("times", availableTimes);
        model.addAttribute("selectedDate", date);
        model.addAttribute("deptId", deptId);

        return "citizen/appointments/slots";
    }

    @GetMapping("/my")
    public String myAppointments(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Appointment> apps = appointmentService.getMyAppointments(userDetails.getUsername());
        for (Appointment app : apps) {
            System.out.println("Appointment ID: " + app.getId());
            if (app.getSlot().getEmployee().getDepartment() == null) {
                System.err.println("!!!EMPLOYEE  " + app.getSlot().getEmployee().getEmail() + " DOES NOT HAVE A DEPARTMENT!");
            } else {
                System.out.println("Department is: " + app.getSlot().getEmployee().getDepartment().getName());
            }
        }

        model.addAttribute("appointments", apps);
        return "citizen/appointments/my";
    }
}