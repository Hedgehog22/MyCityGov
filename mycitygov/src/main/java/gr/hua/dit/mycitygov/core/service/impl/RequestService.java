package gr.hua.dit.mycitygov.core.service.impl;
import gr.hua.dit.mycitygov.core.model.*;
import gr.hua.dit.mycitygov.core.port.FileStoragePort;
import gr.hua.dit.mycitygov.core.repository.AttachmentRepository;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import gr.hua.dit.mycitygov.core.repository.RequestTypeRepository;
import gr.hua.dit.mycitygov.core.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final UserRepository userRepository;
    private final FileStoragePort fileStoragePort;
    private final AttachmentRepository attachmentRepository;

    public RequestService(RequestRepository requestRepository,
                          RequestTypeRepository requestTypeRepository,
                          UserRepository userRepository,
                          AttachmentRepository attachmentRepository,
                          FileStoragePort fileStoragePort) {
        this.requestRepository = requestRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.userRepository = userRepository;
        this.attachmentRepository = attachmentRepository;
        this.fileStoragePort = fileStoragePort;
    }

    public List<Request> getCitizenRequests(Long citizenId) {
        return requestRepository.findByCitizenIdOrderBySubmissionDateDesc(citizenId);
    }

    @Transactional
    public void submitRequest(String userEmail, Long requestTypeId, String description, MultipartFile file) throws IOException {
        User citizen = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));

        RequestType type = requestTypeRepository.findById(requestTypeId).orElseThrow(() -> new RuntimeException("Request Type not found"));

        if (!type.getActive()) {
            throw new RuntimeException("this type of request is inactive!");
        }

        Request request = new Request(citizen, type, description);
        request = requestRepository.save(request);

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();

            List<String> allowedTypes = List.of("application/pdf", "image/jpeg", "image/png");

            if (contentType == null || !allowedTypes.contains(contentType)) {
                throw new RuntimeException("WARNING! Only these formats are allowed: PDF, JPG, PNG.");
            }

            String storageKey = fileStoragePort.uploadFile(file);

            Attachment attachment = new Attachment(
                    file.getOriginalFilename(),
                    storageKey,
                    file.getContentType(),
                    file.getSize(),
                    request,
                    citizen
            );
            attachmentRepository.save(attachment);
        }
    }

    @Transactional
    public void replyToRequest(Long requestId, String userExplanation, MultipartFile file) throws IOException {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != RequestStatus.PENDING_INFO) {
            throw new IllegalStateException("You can only reply to requests that need information.");
        }

        String oldComments = (request.getComments() != null) ? request.getComments() : "";
        String newComments = oldComments + "\n\nCITIZEN REPLY:\n" + userExplanation;
        if (newComments.length() > 4000) {
            newComments = newComments.substring(0, 4000);
        }
        request.setComments(newComments);

        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            List<String> allowedTypes = List.of("application/pdf", "image/jpeg", "image/png");
            if (contentType == null || !allowedTypes.contains(contentType)) {
                throw new RuntimeException("Invalid file type provided in reply.");
            }

            String storageKey = fileStoragePort.uploadFile(file);

            User citizen = request.getCitizen();
            Attachment attachment = new Attachment(
                    file.getOriginalFilename(),
                    storageKey,
                    file.getContentType(),
                    file.getSize(),
                    request,
                    citizen
            );

            attachmentRepository.save(attachment);
            System.out.println("Additional file uploaded to MinIO: " + storageKey);
        }

        request.setStatus(RequestStatus.SUBMITTED);
        requestRepository.save(request);
    }
}