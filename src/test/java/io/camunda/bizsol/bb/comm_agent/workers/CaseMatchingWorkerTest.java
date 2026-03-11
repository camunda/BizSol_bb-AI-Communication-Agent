package io.camunda.bizsol.bb.comm_agent.workers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import io.camunda.bizsol.bb.comm_agent.models.CaseMatchingProcessVariables;
import io.camunda.bizsol.bb.comm_agent.models.CorrelationKey;
import io.camunda.bizsol.bb.comm_agent.models.SupportCase;
import io.camunda.bizsol.bb.comm_agent.services.CaseMatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaseMatchingWorkerTest {

    private static final String CORRELATION_KEY = "correlationKey";
    @Mock private CaseMatchingService caseMatchingService;

    private CaseMatchingWorker workerUnderTest;

    @BeforeEach
    void setUp() {
        workerUnderTest = new CaseMatchingWorker(caseMatchingService);
    }

    @Test
    void shouldCallServiceAndSetResult() {
        // given
        CaseMatchingProcessVariables caseMatchingProcessVariables =
                new CaseMatchingProcessVariables();
        caseMatchingProcessVariables.setSupportCase(SupportCase.builder().build());
        when(caseMatchingService.matchCase(any(SupportCase.class)))
                .thenReturn(new CorrelationKey(CORRELATION_KEY));

        // when
        CaseMatchingProcessVariables result =
                workerUnderTest.matchCase(caseMatchingProcessVariables);

        // then
        assertThat(result).isSameAs(caseMatchingProcessVariables);
        assertThat(result.getCorrelationKey()).isEqualTo(CORRELATION_KEY);
    }
}
