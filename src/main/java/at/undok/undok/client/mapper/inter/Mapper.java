package at.undok.undok.client.mapper.inter;

public interface Mapper<T, R> {

    R toDto(T t);

    T toEntity(R dto);

}
