package at.undok.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TimelineItemDto {

  @NotNull
  private Long id;

  private String name;

  private LocalDateTime counselingDate;

  private LocalDateTime createdAt;

  private UUID counselingId;

  private UUID clientId;

}
