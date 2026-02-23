package io.camunda.bizsol.bb.comm_agent.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SupportCaseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldMarshalAndUnmarshalSupportCaseWithEmailContext() throws Exception {
        // given
        SupportCase supportCase =
                SupportCase.builder()
                        .subject("Subject A")
                        .request("Please help")
                        .receivedDateTime(LocalDateTime.of(2026, 2, 17, 12, 34, 56))
                        .communicationContext(
                                EmailCommunicationContext.builder()
                                        .emailAddress("example@camunda.com")
                                        .conversationId("conv-123")
                                        .build())
                        .attachments(
                                List.of(
                                        Attachment.builder()
                                                .documentId("doc-1")
                                                .storeId("store-9")
                                                .contentHash("hash-xyz")
                                                .metadata(
                                                        new Attachment.Metadata(
                                                                "note.txt", "text/plain", 12L))
                                                .build()))
                        .build();

        // when
        String json = objectMapper.writeValueAsString(supportCase);
        SupportCase roundTrip = objectMapper.readValue(json, SupportCase.class);
        JsonNode root = objectMapper.readTree(json);

        // then
        assertAll(
                () -> assertThat(roundTrip).isEqualTo(supportCase),
                () ->
                        assertThat(root.get("communicationContext").get("channel").asText())
                                .isEqualTo("email"));
    }

    @Test
    void shouldUnmarshalSupportCaseFromJsonWithPhoneContext() throws Exception {
        // given
        String json =
                "{"
                        + "\"subject\":\"Subject B\","
                        + "\"request\":\"Call me\","
                        + "\"receivedDateTime\":\"2026-02-18T09:00:00\","
                        + "\"communicationContext\":{"
                        + "\"channel\":\"phone\","
                        + "\"conversationId\":\"conv-456\","
                        + "\"phoneNumber\":\"+1-202-555-0100\""
                        + "},"
                        + "\"attachments\":[]"
                        + "}";

        // when
        SupportCase supportCase = objectMapper.readValue(json, SupportCase.class);
        PhoneCommunicationContext context =
                (PhoneCommunicationContext) supportCase.communicationContext();

        // then
        assertAll(
                () -> assertThat(supportCase.subject()).isEqualTo("Subject B"),
                () ->
                        assertThat(supportCase.communicationContext())
                                .isInstanceOf(PhoneCommunicationContext.class),
                () -> assertThat(context.channel()).isEqualTo("phone"),
                () -> assertThat(context.conversationId()).isEqualTo("conv-456"),
                () -> assertThat(context.phoneNumber()).isEqualTo("+1-202-555-0100"));
    }

    @Test
    void shouldMarshalAndUnmarshalSupportCaseWithPhoneContext() throws Exception {
        // given
        SupportCase supportCase =
                SupportCase.builder()
                        .subject("Subject C")
                        .request("Please call back")
                        .receivedDateTime(LocalDateTime.of(2026, 2, 19, 8, 15))
                        .communicationContext(
                                PhoneCommunicationContext.builder()
                                        .conversationId("conv-789")
                                        .phoneNumber("+1-202-555-0111")
                                        .build())
                        .attachments(List.of())
                        .build();

        // when
        String json = objectMapper.writeValueAsString(supportCase);
        SupportCase roundTrip = objectMapper.readValue(json, SupportCase.class);
        JsonNode root = objectMapper.readTree(json);

        // then
        assertAll(
                () -> assertThat(roundTrip).isEqualTo(supportCase),
                () ->
                        assertThat(root.get("communicationContext").get("channel").asText())
                                .isEqualTo("phone"));
    }

    @Test
    void shouldUnmarshalSupportCaseFromJsonWithEmailContext() throws Exception {
        // given
        String json =
                "{"
                        + "\"subject\":\"Subject D\","
                        + "\"request\":\"Email me\","
                        + "\"receivedDateTime\":\"2026-02-20T10:30:00\","
                        + "\"communicationContext\":{"
                        + "\"channel\":\"email\","
                        + "\"conversationId\":\"conv-321\","
                        + "\"emailAddress\":\"contact@camunda.com\""
                        + "},"
                        + "\"attachments\":[]"
                        + "}";

        // when
        SupportCase supportCase = objectMapper.readValue(json, SupportCase.class);
        EmailCommunicationContext context =
                (EmailCommunicationContext) supportCase.communicationContext();

        // then
        assertAll(
                () -> assertThat(supportCase.subject()).isEqualTo("Subject D"),
                () ->
                        assertThat(supportCase.communicationContext())
                                .isInstanceOf(EmailCommunicationContext.class),
                () -> assertThat(context.channel()).isEqualTo("email"),
                () -> assertThat(context.conversationId()).isEqualTo("conv-321"),
                () -> assertThat(context.emailAddress()).isEqualTo("contact@camunda.com"));
    }
}
