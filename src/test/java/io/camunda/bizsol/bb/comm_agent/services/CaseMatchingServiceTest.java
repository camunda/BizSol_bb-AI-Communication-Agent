package io.camunda.bizsol.bb.comm_agent.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CaseMatchingServiceTest {

    @Mock private RestTemplate restTemplate;

    private CaseMatchingService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new CaseMatchingService();
    }
}
