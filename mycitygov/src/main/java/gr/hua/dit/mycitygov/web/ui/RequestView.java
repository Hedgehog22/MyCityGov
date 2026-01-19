package gr.hua.dit.mycitygov.web.ui;

import java.time.Instant;

public record RequestView(
        String protocolNumber,
        String status,
        String requestType,
        Instant submissionDate
) {}