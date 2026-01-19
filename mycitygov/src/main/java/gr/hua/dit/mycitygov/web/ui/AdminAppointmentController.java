package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Role;
import gr.hua.dit.mycitygov.core.repository.DepartmentRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;

import gr.hua.dit.mycitygov.core.service.impl.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;


@Controller
@RequestMapping("/admin/appointments")
public class AdminAppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    public AdminAppointmentController(AppointmentService appointmentService, UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }




    @GetMapping("/generate")
    public String showGenerateForm(Model model) {
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("employees", userRepository.findByRole(Role.EMPLOYEE));
        model.addAttribute("defaultDate", LocalDate.now().plusDays(1));
        return "admin/appointments/generate-slots";
    }

    @PostMapping("/delete-day")
    public String deleteSlotsForDay(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("date") LocalDate date,
            RedirectAttributes redirectAttributes) {
        try {
            appointmentService.deleteSlotsForDay(employeeId, date);
            redirectAttributes.addFlashAttribute("successMessage", "SLots for" + date + " successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deleting ERROR: " + e.getMessage());
        }
        return "redirect:/admin/appointments/generate";
    }


    @PostMapping("/generate")
    public String generateSlots(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate,
            @RequestParam("startTime") LocalTime startTime,
            @RequestParam("endTime") LocalTime endTime,

            @RequestParam(value = "breakStart", required = false) LocalTime breakStart,
            @RequestParam(value = "breakEnd", required = false) LocalTime breakEnd,
            @RequestParam(value = "skipWeekends", defaultValue = "true") boolean skipWeekends, RedirectAttributes redirectAttributes) {

        try {
            appointmentService.generateSlotsForRange(employeeId, startDate, endDate, startTime, endTime, skipWeekends, breakStart, breakEnd);
            redirectAttributes.addFlashAttribute("successMessage", "Schedule was successfully generated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
        }
        return "redirect:/admin/appointments/generate";
    }
}