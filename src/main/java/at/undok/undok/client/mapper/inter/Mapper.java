package at.undok.undok.client.mapper.inter;

public interface Mapper<E, D> {

    D toDto(E e);

    E toEntity(D dto);

}
