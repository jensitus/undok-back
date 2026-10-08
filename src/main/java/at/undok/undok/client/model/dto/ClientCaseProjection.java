package at.undok.undok.client.model.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The one case that currently represents a client, flattened for the clients list.
 * Used to batch-load case status for a whole client list in a single query, instead of
 * going through getClientById per client — the list path never populates openCase.
 */
public interface ClientCaseProjection {

    UUID getClientId();

    UUID getCaseId();

    String getStatus();

    LocalDate getStartDate();

    LocalDate getEndDate();

    String getReferredTo();

}
