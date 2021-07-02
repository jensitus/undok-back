package at.undok.common.util;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.util.UUID;

@Component
public class UUIDConverter implements AttributeConverter<UUID, String> {

  @Override
  public String convertToDatabaseColumn(UUID uuid) {
    return uuid == null ? null: uuid.toString();
  }

  @Override
  public UUID convertToEntityAttribute(String s) {
    return s == null ? null : UUID.fromString(s);
  }
}
