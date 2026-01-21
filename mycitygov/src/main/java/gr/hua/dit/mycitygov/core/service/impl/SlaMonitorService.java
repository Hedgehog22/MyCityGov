package gr.hua.dit.mycitygov.core.service.impl;


import gr.hua.dit.mycitygov.core.model.Request;
import gr.hua.dit.mycitygov.core.model.RequestStatus;
import gr.hua.dit.mycitygov.core.repository.RequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class SlaMonitorService {
    private final RequestRepository requestRepository;

    public SlaMonitorService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Scheduled(cron = "*/30 * * * * *") //for testing purposes
    @Transactional
    public void checkSlaBreaches() {
        System.out.println("Checking for overdue requests...");

        Instant now = Instant.now();

        List<RequestStatus> activeStatuses = List.of(RequestStatus.SUBMITTED, RequestStatus.IN_PROGRESS);
        List<Request> activeRequests = requestRepository.findByStatusIn(activeStatuses);

        int newBreaches = 0;
        for (Request req : activeRequests) {
            if (req.isSlaBreached()) {
                continue;
            }

            long daysPassed = ChronoUnit.DAYS.between(req.getSubmissionDate(), now);
            int slaLimit = req.getRequestType().getSlaDays();

            if (daysPassed > slaLimit) {
                req.setSlaBreached(true);
                newBreaches++;
            }
        }
        requestRepository.saveAll(activeRequests);

        System.out.println("Overdue: " + newBreaches);
    }
}