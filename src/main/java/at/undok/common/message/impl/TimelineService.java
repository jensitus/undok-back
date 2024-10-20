package at.undok.common.message.impl;

import at.undok.common.encryption.AttributeEncryptor;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.repository.CounselingRepo;
import org.modelmapper.ModelMapper;
import at.undok.common.dto.TimelineItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimelineService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private CounselingRepo counselingRepo;

  @Autowired
  private AttributeEncryptor attributeEncryptor;

  public List<TimelineItemDto> getItemsForTimeline() {
    List<TimelineItemDto> timelineItemDtoList = getTimelineDtos();
    List<TimelineItemDto> dueDateFirstList = timelineItemDtoList.stream().filter(x -> x.getCounselingDate() != null).collect(Collectors.toList());

    return dueDateFirstList;
  }

  public Map<String, ?> getItemsGroupedByMonth(Long user_id) {
//    List<TimelineItemDto> timelineItemDtos = getTimelineDtos(user_id);
//    Map timelineItemMap = timelineItemDtos.stream().collect(Collectors.groupingBy(i -> i.getCreatedAt().getMonth()));
//    for (TimelineItemDto dto : timelineItemDtos) {
//      dto.getCreatedAt().getMonth();
//    }
    Map timelineItemMap = new HashMap();
    return timelineItemMap;

  }

  private List<TimelineItemDto> getTimelineDtos() {
    List<TimelineItemDto> timelineItemDtos = new ArrayList<>();

    List<Counseling> counselingsInFuture = counselingRepo.findAllInFuture(LocalDateTime.now());
    for (Counseling c : counselingsInFuture) {
      TimelineItemDto timelineItemDto = new TimelineItemDto();
      timelineItemDto.setCounselingDate(c.getCounselingDate());
      timelineItemDto.setCounselingId(c.getId());
      timelineItemDto.setCreatedAt(c.getCreatedAt());
      if (c.getClient().getPerson().getFirstName() != null && c.getClient().getPerson().getLastName() != null) {
        timelineItemDto.setName(c.getClient().getPerson().getFirstName() + ' ' + c.getClient().getPerson().getLastName());
      } else {
        timelineItemDto.setName(c.getClient().getKeyword());
      }
      timelineItemDto.setClientId(c.getClient().getId());
      timelineItemDtos.add(timelineItemDto);
    }

    return timelineItemDtos;
  }



}
