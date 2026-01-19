package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Attachment;
import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestStatus;
import gr.hua.dit.mycitygov.core.model.User;
import gr.hua.dit.mycitygov.core.port.FileStoragePort;
import gr.hua.dit.mycitygov.core.repository.AttachmentRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;


import gr.hua.dit.mycitygov.core.service.impl.AppointmentService;
import gr.hua.dit.mycitygov.core.service.impl.EmployeeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStoragePort fileStoragePort;
    private final AppointmentService appointmentService;

    public EmployeeController(EmployeeService employeeService,
                              UserRepository userRepository,
                              RequestRepository requestRepository,
                              AttachmentRepository attachmentRepository,
                              FileStoragePort fileStoragePort, AppointmentService appointmentService) {
        this.employeeService = employeeService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStoragePort = fileStoragePort;
        this.appointmentService = appointmentService;
    }


    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User employee = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("employee", employee);

        Long deptId = (employee.getDepartment() != null) ? employee.getDepartment().getId() : null;

        if (deptId != null) {
            model.addAttribute("unassignedRequests", employeeService.getUnassignedRequests(deptId));
        } else {
            model.addAttribute("unassignedRequests", java.util.Collections.emptyList());
        }

        model.addAttribute("myRequests", employeeService.getMyRequests(employee.getId()));
        model.addAttribute("appointments", employeeService.getAllAppointments(employee.getId()));

        return "employee/dashboard";
    }


    @GetMapping("/requests/{requestId}/attachments/{attachmentId}/download")
    public String downloadAttachment(@PathVariable Long requestId,
                                     @PathVariable Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        String url = fileStoragePort.getFileUrl(attachment.getStorageKey());
        return "redirect:" + url;
    }


    @GetMapping("/requests/{id}/download-result")
    public String downloadResult(@PathVariable Long id) {
        Request request = requestRepository.findById(id).orElseThrow();
        if (request.getResultDocumentKey() == null) {
            throw new RuntimeException("No result document found.");
        }
        String url = fileStoragePort.getFileUrl(request.getResultDocumentKey());
        return "redirect:" + url;
    }



    @PostMapping("/requests/{id}/claim")
    public String claimRequest(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            employeeService.claimRequest(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Request assigned to you!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }

    @PostMapping("/requests/{id}/process")
    public String processRequest(@PathVariable Long id,
                                 @RequestParam("action") String action,
                                 @RequestParam(value = "comments", required = false) String comments,
                                 @RequestParam(value = "resultFile", required = false) MultipartFile resultFile,
                                 RedirectAttributes redirectAttributes) {
        try {
            RequestStatus newStatus;
            switch (action) {
                case "approve": newStatus = RequestStatus.APPROVED; break;
                case "reject":  newStatus = RequestStatus.REJECTED; break;
                case "info":    newStatus = RequestStatus.PENDING_INFO; break;
                default: throw new IllegalArgumentException("Unknown action");
            }

            employeeService.processRequest(id, newStatus, comments, resultFile);
            redirectAttributes.addFlashAttribute("successMessage", "Request updated to: " + newStatus);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }

    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointmentByEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }


    @PostMapping("/appointments/{id}/complete")
    public String completeAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appointmentService.completeAppointment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Appointment completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/employee/dashboard";
    }

}