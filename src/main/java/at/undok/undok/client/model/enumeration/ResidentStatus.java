package at.undok.undok.client.model.enumeration;

public enum ResidentStatus {

    ASYL("asyl"),
    EU("eu"),
    ILLEGAL("illegal");

    public final String label;

    private ResidentStatus(String label) {
        this.label = label;
    }

}
