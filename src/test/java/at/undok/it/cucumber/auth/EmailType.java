package at.undok.it.cucumber.auth;

public enum EmailType {
    CONFIRMATION("[undok] confirm account");

    private final String subject;
    private final String mediaType = "text/html;charset=UTF-8";

    EmailType(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getMediaType() {
        return mediaType;
    }
}
