package at.undok.undok.client.model.enumeration;

public enum MaritalStatus {

    SINGLE("Ledig"),
    MARRIED("Verheiratet"),
    DIVORCED("Geschieden"),
    WIDOWED("Verwitwet"),
    REGISTERED_PARTNERSHIP("Eingetragene Partnerschaft"),
    DISSOLVED_REGISTERED_PARTNERSHIP("Aufgelöste eingetragene Partnerschaft"),
    SURVIVING_REGISTERED_PARTNERSHIP("Hinterbliebener eingetragene Partnerschaft");

    public final String label;

    private MaritalStatus(String label) {
        this.label = label;
    }

}
