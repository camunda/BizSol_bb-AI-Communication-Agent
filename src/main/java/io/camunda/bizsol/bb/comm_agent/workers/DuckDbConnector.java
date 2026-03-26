package io.camunda.bizsol.bb.comm_agent.workers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.bizsol.bb.comm_agent.models.DuckDbResult;
import io.camunda.bizsol.bb.comm_agent.models.DuckDbVariables;
import io.camunda.bizsol.bb.comm_agent.services.DuckDbService;
import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import org.springframework.stereotype.Component;

@OutboundConnector(
        name = "DuckDb",
        inputVariables = {"operation", "id", "payload"},
        type = "DuckDb")
@Component
public class DuckDbConnector implements OutboundConnectorFunction {

    private final DuckDbService duckDbService;
    private final ObjectMapper objectMapper;

    public DuckDbConnector(DuckDbService duckDbService, ObjectMapper objectMapper) {
        this.duckDbService = duckDbService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Object execute(OutboundConnectorContext context) {
        var variables = context.bindVariables(DuckDbVariables.class);
        return switch (variables.operation().toUpperCase()) {
            case "CREATE" ->
                    new DuckDbResult(
                            duckDbService.create(
                                    variables.id(), toJson(variables.payload())));
            case "READ" -> new DuckDbResult(duckDbService.read(variables.id()).orElse(null));
            case "UPDATE" ->
                    new DuckDbResult(
                            duckDbService.update(
                                    variables.id(), toJson(variables.payload())));
            case "DELETE" -> {
                duckDbService.delete(variables.id());
                yield new DuckDbResult(null);
            }
            case "LIST" -> new DuckDbResult(duckDbService.list());
            default ->
                    throw new IllegalArgumentException(
                            "Unknown operation: " + variables.operation());
        };
    }

    private String toJson(Object payload) {
        if (payload == null) return null;
        if (payload instanceof String s) return s;
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Cannot serialize payload to JSON", e);
        }
    }
}
