package gr.hua.dit.mycitygov.web.ui;

import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestStatus;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.service.UserService;
import gr.hua.dit.mycitygov.core.service.impl.DepartmentService;
import gr.hua.dit.mycitygov.core.service.impl.RequestService;
import gr.hua.dit.mycitygov.core.service.impl.RequestTypeService;
import gr.hua.dit.mycitygov.web.ui.model.RequestTypeForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RequestTypeService requestTypeService;
    private final DepartmentService departmentService;
    private final RequestRepository requestService;

    public AdminController(UserService userService,
                           RequestTypeService requestTypeService,
                           DepartmentService departmentService, RequestRepository requestService) {
        this.userService = userService;
        this.requestTypeService = requestTypeService;
        this.departmentService = departmentService;
        this.requestService = requestService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long totalRequests = requestService.count();

        long submitted = requestService.countByStatus(RequestStatus.SUBMITTED);
        long inProgress = requestService.countByStatus(RequestStatus.IN_PROGRESS);
        long done = requestService.countByStatus(RequestStatus.APPROVED) + requestService.countByStatus(RequestStatus.REJECTED);
        long overdueCount = requestService.countBySlaBreachedTrue();

        model.addAttribute("totalCitizens", userService.countAllCitizens());
        model.addAttribute("totalRequestsType", requestTypeService.countAllRequests()); //TO DO: more statistic
        model.addAttribute("activeServices", departmentService.countActiveDepartments());
        model.addAttribute("cntTotalRequests", totalRequests);
        model.addAttribute("cntPending", submitted);
        model.addAttribute("cntInProgress", inProgress);
        model.addAttribute("cntDone", done);

        model.addAttribute("requestTypes", requestTypeService.findAll());
        model.addAttribute("departments", departmentService.findAll());

        model.addAttribute("overdueCount", overdueCount);

        if (!model.containsAttribute("requestTypeForm")) {
            model.addAttribute("requestTypeForm", new RequestTypeForm(null, null, null, null));
        }

        return "admin/dashboard";
    }

    @PostMapping("/types/add")
    public String addRequestType(
            @Valid @ModelAttribute("requestTypeForm") RequestTypeForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.requestTypeForm", bindingResult);
            redirectAttributes.addFlashAttribute("requestTypeForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", "Check the fields.");
            return "redirect:/admin/dashboard";
        }
        try {
            requestTypeService.createType(
                    form.name(),
                    form.description(),
                    form.slaDays(),
                    form.departmentId()
            );
            redirectAttributes.addFlashAttribute("successMessage", "New type created!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/departments/add")
    public String addDepartment(@RequestParam("name") String name,
                                @RequestParam("startTime") String startTime,
                                @RequestParam("endTime") String endTime,
                                RedirectAttributes redirectAttributes) {
        try {
            if (name == null || name.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Name is required.");
                return "redirect:/admin/dashboard";
            }

            departmentService.createDepartment(name, startTime, endTime);

            redirectAttributes.addFlashAttribute("successMessage", "Departament was successfully created!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "DB ERROR: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @PostMapping("/types/toggle")
    public String toggleRequestType(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            requestTypeService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ooops... something went wrong");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/departments/schedule")
    public String updateSchedule(@RequestParam("departmentId") Long deptId,
                                 @RequestParam("startTime") String startStr,
                                 @RequestParam("endTime") String endStr,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalTime openTime = LocalTime.parse(startStr);
            LocalTime closeTime = LocalTime.parse(endStr);

            departmentService.updateSchedule(deptId, openTime, closeTime);

            redirectAttributes.addFlashAttribute("successMessage", "Schedule successfully updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ERROR: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";

    }

    @GetMapping("/reports/overdue")
    public String showOverdueReports(Model model) {
        List<Request> overdueList = requestService.findBySlaBreachedTrue();
        model.addAttribute("requests", overdueList);
       // model.addAttribute("reportTitle", "!Overdue Requests!");
        return "admin/requests-report";
    }

}