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
import gr.hua.dit.mycitygov.core.service.impl.RequestService;
import gr.hua.dit.mycitygov.core.service.impl.RequestTypeService;
import gr.hua.dit.mycitygov.web.ui.model.RequestSubmitForm;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/citizen")
public class CitizenController {

    private final RequestService requestService;
    private final RequestTypeService requestTypeService;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final FileStoragePort fileStoragePort;
    private final AttachmentRepository attachmentRepository;
    private final AppointmentService appointmentService;

    public CitizenController(RequestService requestService,
                             RequestTypeService requestTypeService,
                             UserRepository userRepository, RequestRepository requestRepository, FileStoragePort fileStoragePort, AttachmentRepository attachmentRepository, AppointmentService appointmentService) {
        this.requestService = requestService;
        this.requestTypeService = requestTypeService;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.fileStoragePort = fileStoragePort;
        this.attachmentRepository = attachmentRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("requestTypes", requestTypeService.findAllActive());
        if (!model.containsAttribute("requestForm")) {
            model.addAttribute("requestForm", new RequestSubmitForm(null, "", null));
        }
        return "citizen/dashboard";
    }

    @PostMapping("/requests/new")
    public String submitRequest(
            @Valid @ModelAttribute("requestForm") RequestSubmitForm form,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.requestForm", bindingResult);
            redirectAttributes.addFlashAttribute("requestForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", "PLease check your input.");
            return "redirect:/citizen/dashboard";
        }

        try {
            requestService.submitRequest(
                    userDetails.getUsername(),
                    form.requestTypeId(),
                    form.description(),
                    form.attachment()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Thank you for applying! For more information click on Request History");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error while applying: " + e.getMessage());
        }
        return "redirect:/citizen/dashboard";
    }

    @GetMapping("/my-requests")
    public String showMyRequests(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User citizen = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Request> requests = requestService.getCitizenRequests(citizen.getId());
        model.addAttribute("requests", requests);

        return "citizen/my-requests-list";
    }

    @GetMapping("/requests/{id}/download/{attachmentId}")
    public String downloadAttachment(@PathVariable Long id, @PathVariable Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId).orElseThrow(() -> new RuntimeException("Attachment not found"));

        String url = fileStoragePort.getFileUrl(attachment.getStorageKey());

        return "redirect:" + url;
    }

    @GetMapping("/requests/{id}/download-result")
    public String downloadResult(@PathVariable Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getResultDocumentKey() == null) {
            throw new RuntimeException("Document not ready yet.");
        }

        String url = fileStoragePort.getFileUrl(request.getResultDocumentKey());
        return "redirect:" + url;
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@RequestParam Long deptId,
                                  @RequestParam LocalDate date,
                                  @RequestParam LocalTime time,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            appointmentService.bookAppointment(deptId, date, time, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment booked successfully!");
            return "redirect:/citizen/appointments/my";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/citizen/appointments/slots?deptId=\" + deptId + \"&date=\" + date;";
        }
    }


    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointmentByCitizen(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        try {
            appointmentService.cancelAppointmentByCitizen(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully. The slot is now free.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling appointment: " + e.getMessage());
        }
        return "redirect:/citizen/appointments/my";
    }

    @GetMapping("/requests/{id}/reply")
    public String showReplyForm(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Request request = requestRepository.findById(id).orElseThrow();

            if (!request.getCitizen().getEmail().equals(userDetails.getUsername())) {
                throw new RuntimeException("Access Denied");
            }

            if (request.getStatus() != RequestStatus.PENDING_INFO) {
                redirectAttributes.addFlashAttribute("errorMessage", "This request does not require action.");
                return "redirect:/citizen/my-requests";
            }

            model.addAttribute("request", request);
            return "citizen/request-reply";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/citizen/my-requests";
        }
    }


    @PostMapping("/requests/{id}/reply")
    public String processReply(@PathVariable Long id,
                               @RequestParam("explanation") String explanation,
                               @RequestParam(value = "attachment", required = false) MultipartFile attachment,
                               RedirectAttributes redirectAttributes) {
        try {
            requestService.replyToRequest(id, explanation, attachment);
            redirectAttributes.addFlashAttribute("successMessage", "Your reply has been sent to the employee.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reply: " + e.getMessage());
        }
        return "redirect:/citizen/my-requests";
    }
}