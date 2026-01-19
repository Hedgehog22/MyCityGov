package gr.hua.dit.mycitygov.core.service.impl;

import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.port.FileStoragePort;
import gr.hua.dit.mycitygov.core.repository.AppointmentRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class EmployeeService {

    private final RequestRepository requestRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final FileStoragePort fileStoragePort;

    public EmployeeService(RequestRepository requestRepository,
                           AppointmentRepository appointmentRepository,
                           UserRepository userRepository, FileStoragePort fileStoragePort) {
        this.requestRepository = requestRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public List<Request> getUnassignedRequests(Long deptId) {
        return requestRepository.findByRequestTypeDepartmentIdAndEmployeeIsNullOrderBySubmissionDateDesc(deptId);
    }

    public List<Request> getMyRequests(Long employeeId) {
        return requestRepository.findByEmployeeIdOrderBySubmissionDateDesc(employeeId);
    }

    @Transactional
    public void claimRequest(Long requestId, String employeeEmail) {
        User employee = userRepository.findByEmail(employeeEmail).orElseThrow();
        Request request = requestRepository.findById(requestId).orElseThrow();

        if (!request.getRequestType().getDepartment().getId().equals(employee.getDepartment().getId())) {
            throw new RuntimeException("This request belongs to another department!");
        }

        request.setEmployee(employee);
        request.setStatus(RequestStatus.IN_PROGRESS);
        requestRepository.save(request);
    }

    @Transactional
    public void processRequest(Long requestId, RequestStatus newStatus, String comments, MultipartFile resultFile) throws IOException {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));

        if (newStatus == RequestStatus.PENDING_INFO) {
            if (comments == null ||  comments.trim().isEmpty()) {
                throw new IllegalArgumentException("Error: You must write a comment to request more info.");
            }
        }

        if (newStatus == RequestStatus.REJECTED) {
            if (comments == null || comments.trim().isEmpty()) {
                throw new IllegalArgumentException("Error: You must provide a reason for rejection.");
            }
        }

        if (newStatus == RequestStatus.APPROVED){
            if (resultFile == null || resultFile.isEmpty()) {
                throw new IllegalArgumentException("Error: You cannot approve a request without attaching the result document.");
            }
            String resultKey = fileStoragePort.uploadFile(resultFile);
            request.setResultDocumentKey(resultKey);
            //requestRepository.save(request);
        }

        request.setStatus(newStatus);
        if (comments != null && !comments.trim().isEmpty()) {
            request.setComments(comments);
        }
        requestRepository.save(request);
    }

    public List<Appointment> getAllAppointments(Long employeeId) {
        return appointmentRepository.findAllForEmployee(employeeId);}

    //TO DO
    @Transactional
    public void cancelAppointment(Long appointmentId, String reason) {
        Appointment app = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));
        app.setStatus(AppointmentStatus.CANCELLED);
        if (app.getSlot() != null) {
            AppointmentSlot slot = app.getSlot();
            slot.setIsAvailable(true);
        }
        appointmentRepository.save(app);
    }
}