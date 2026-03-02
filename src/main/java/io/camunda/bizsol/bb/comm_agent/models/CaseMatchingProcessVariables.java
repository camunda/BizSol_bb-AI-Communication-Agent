package io.camunda.bizsol.bb.comm_agent.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CaseMatchingProcessVariables {
    private SupportCase supportCase;
    private String correlationKey;
}
