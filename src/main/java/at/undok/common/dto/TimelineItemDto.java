package at.undok.common.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
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
