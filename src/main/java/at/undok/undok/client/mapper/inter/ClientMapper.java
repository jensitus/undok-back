package at.undok.undok.client.mapper.inter;

import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.entity.Client;

public interface ClientMapper extends Mapper<Client, ClientDto> {

    @Override
    ClientDto toDto(Client client);

    @Override
    Client toEntity(ClientDto dto);

}
