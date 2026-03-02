package io.camunda.bizsol.bb.comm_agent.services;

import io.camunda.bizsol.bb.comm_agent.models.CorrelationKey;
import io.camunda.bizsol.bb.comm_agent.models.EmailCommunicationContext;
import io.camunda.bizsol.bb.comm_agent.models.PhoneCommunicationContext;
import io.camunda.bizsol.bb.comm_agent.models.SupportCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaseMatchingService {

    private static final Logger log = LoggerFactory.getLogger(CaseMatchingService.class);

    public CorrelationKey matchCase(SupportCase supportCase) {
        final String correlationKey =
                switch (supportCase.communicationContext()) {
                    case EmailCommunicationContext c -> c.emailAddress();
                    case PhoneCommunicationContext c -> c.phoneNumber();
                    default -> supportCase.communicationContext().conversationId();
                };
        log.info("Match incoming support case to correlation: " + correlationKey);
        return new CorrelationKey(correlationKey);
    }
}
