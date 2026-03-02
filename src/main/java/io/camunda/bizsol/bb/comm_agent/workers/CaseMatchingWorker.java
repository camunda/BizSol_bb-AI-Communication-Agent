package io.camunda.bizsol.bb.comm_agent.workers;

import io.camunda.bizsol.bb.comm_agent.models.CaseMatchingProcessVariables;
import io.camunda.bizsol.bb.comm_agent.models.CorrelationKey;
import io.camunda.bizsol.bb.comm_agent.models.SupportCase;
import io.camunda.bizsol.bb.comm_agent.services.CaseMatchingService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.VariablesAsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CaseMatchingWorker {
    private static final Logger log = LoggerFactory.getLogger(CaseMatchingWorker.class);
    private CaseMatchingService caseMatchingService;

    public CaseMatchingWorker(CaseMatchingService caseMatchingService) {
        this.caseMatchingService = caseMatchingService;
    }

    @JobWorker(type = "CaseMatching")
    public CaseMatchingProcessVariables matchCase(
            @VariablesAsType CaseMatchingProcessVariables caseMatchingProcessVariables) {
        // Retrieve relevant process variables
        SupportCase supportCase = caseMatchingProcessVariables.getSupportCase();
        log.info("Received SupportCase: {}", supportCase);

        // call external service
        CorrelationKey correlationKey = caseMatchingService.matchCase(supportCase);

        // add to process variables for further processing
        caseMatchingProcessVariables.setCorrelationKey(correlationKey.value());
        return caseMatchingProcessVariables;
    }
}
