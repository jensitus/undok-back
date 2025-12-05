package at.undok.undok.client.mapper.impl;

import at.undok.undok.client.mapper.inter.ClientMapper;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.entity.Client;

public class ClientMapperImpl implements ClientMapper {

    @Override
    public ClientDto toDto(Client client) {
        return new ClientDto(client.getId(),
                             client.getCreatedAt(),
                             client.getUpdatedAt(),
                             client.getKeyword(),
                             client.getEducation(),
                             client.getMaritalStatus(),
                             client.getNationality(),
                             client.getLanguage(),
                             client.getInterpreterNecessary(),
                             client.getHowHasThePersonHeardFromUs(),
                             client.getCurrentResidentStatus(),
                             client.getVulnerableWhenAssertingRights(),
                             client.getFormerResidentStatus(),
                             client.getLabourMarketAccess(),
                             client.getPosition(),
                             client.getSector(),
                             client.getUnion(),
                             client.getMembership(),
                             client.getOrganization(),
                             client.getSocialInsuranceNumber(),
                             client.getFurtherContact(),
                             client.getComment(),
                             client.getAlert(),
                             client.getFirstName(),
                             client.getLastName(),
                             client.getGender(),
                             client.getTelephone(),
                             client.getEmail(),
                             client.getCity());
    }

    @Override
    public Client toEntity(ClientDto dto) {
        return null;
    }

}
