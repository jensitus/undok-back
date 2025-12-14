package at.undok.undok.client.model.dto;

public record LanguageCount(String language, Long count) {
    public static LanguageCount from(KeyCountProjection projection) {
        return new LanguageCount(projection.getKey(), projection.getCount());
    }
}
