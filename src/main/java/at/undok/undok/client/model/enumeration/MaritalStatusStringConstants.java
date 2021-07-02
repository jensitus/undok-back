package at.undok.undok.client.model.enumeration;

import lombok.Data;

@Data
public class MaritalStatusStringConstants {
    private static final String SINGLE = "Ledig";
    private static final String MARRIED = "Verheiratet";
    private static final String DIVORCED = "Geschieden";
    private static final String WIDOWED = "Verwitwet";
    private static final String REGISTERED_PARTNERSHIP ="Eingetragene Partnerschaft";
    private static final String DISSOLVED_REGISTERED_PARTNERSHIP = "Aufgelöste eingetragene Partnerschaft";
    private static final String SURVIVING_REGISTERED_PARTNERSHIP ="Hinterbliebener eingetragene Partnerschaft";
}
