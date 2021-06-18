package at.undok.undok.client.model.enumeration;

public enum LabourMarketAccess {
    FREE_ACCESS("freier Zugang"),
    ACCESS_WITH_PERMISSION("Zugang mit Berechtigung"),
    INDEPENDENT_ACCESS("selbständiger Zugang"),
    NO_ACCESS("kein Zugang");

    public final String label;

    LabourMarketAccess(String label) {
        this.label = label;
    }

}
