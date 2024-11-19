package at.undok.common.util;

import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import java.util.UUID;

@Component
@Converter
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
