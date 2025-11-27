package at.undok.undok.client.model.dto;

public record CounselingActivityCount(String activity, Long count) {
    public static CounselingActivityCount from(KeyCountProjection projection) {
        return new CounselingActivityCount(projection.getKey(), projection.getCount());
    }
}
