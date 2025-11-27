package at.undok.undok.client.model.dto;

public record GenderCount(String gender, Long count) {
    public static GenderCount from(KeyCountProjection projection) {
        return new GenderCount(projection.getKey(), projection.getCount());
    }
}
