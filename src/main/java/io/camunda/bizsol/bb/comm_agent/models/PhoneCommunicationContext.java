package io.camunda.bizsol.bb.comm_agent.models;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record PhoneCommunicationContext(String conversationId, String phoneNumber)
        implements CommunicationContext {
    @Override
    public String channel() {
        return "phone";
    }
}
