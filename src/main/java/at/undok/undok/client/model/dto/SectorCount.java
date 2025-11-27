package at.undok.undok.client.model.dto;

public record SectorCount(String sector, Long count) {
    public static SectorCount from(KeyCountProjection projection) {
        return new SectorCount(projection.getKey(), projection.getCount());
    }
}
