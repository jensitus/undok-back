package at.undok.undok.client.model.enumeration;

public enum ContactType {
    TELEPHONIC("telefonisch"),
    PERSONAL("persönlich");

    public final String type;

    ContactType(String type) {
        this.type = type;
    }

}
