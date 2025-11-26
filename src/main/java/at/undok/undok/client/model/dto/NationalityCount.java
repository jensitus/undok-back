package at.undok.undok.client.model.dto;

public record NationalityCount(String nationality, Long count) {
    public static NationalityCount from(KeyCountProjection projection) {
        return new NationalityCount(projection.getKey(), projection.getCount());
    }
}
