package at.undok.undok.client.util;

import at.undok.undok.client.model.enumeration.MaritalStatus;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

public class MaritalStatusConverter implements AttributeConverter<MaritalStatus, String> {

    @Override
    public String convertToDatabaseColumn(MaritalStatus maritalStatus) {
        return maritalStatus.name();
    }

    @Override
    public MaritalStatus convertToEntityAttribute(String s) {
        return MaritalStatus.valueOf(s);
    }
}
