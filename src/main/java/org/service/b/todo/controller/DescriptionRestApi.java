package org.service.b.todo.controller;

import org.service.b.common.message.Message;
import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.form.DescriptionForm;
import org.service.b.todo.model.Description;
import org.service.b.todo.service.DescriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"https://www.service-b.org", "https://service-b.org", "http://localhost:4200", "http://localhost:8080"}, maxAge = 3600)
@RequestMapping("/service/todos/{todo_id}/items")
public class DescriptionRestApi {

  private static final Logger logger = LoggerFactory.getLogger(DescriptionRestApi.class);

  @Autowired
  private DescriptionService descriptionService;

  @GetMapping("/{item_id}/descriptions")
  public ResponseEntity getDescription(@PathVariable("item_id") Long item_id) {
    List<DescriptionDto> descriptionDtos = descriptionService.getDescriptionsByItemId(item_id);
    return new ResponseEntity(descriptionDtos, HttpStatus.OK);
  }

  @PostMapping("/{item_id}/descriptions/create")
  public ResponseEntity createDescription(@RequestBody DescriptionForm descriptionForm, @PathVariable("item_id") Long item_id) {
    logger.info("Description Form: " + descriptionForm);
    DescriptionDto descriptionDto = descriptionService.createDescription(descriptionForm, item_id);
    return new ResponseEntity(descriptionDto, HttpStatus.OK);
  }

  @PutMapping("/{item_id}/descriptions/{description_id}/update")
  public ResponseEntity updateDescription(@RequestBody DescriptionForm descriptionForm, @PathVariable("item_id") Long item_id, @PathVariable("description_id") Long description_id) {
    logger.info("description: " + descriptionForm.toString());
    DescriptionDto descriptionDto = descriptionService.updateDescription(descriptionForm, item_id);
    return new ResponseEntity(new Message("jessas"), HttpStatus.OK);
  }

}
