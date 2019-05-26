package org.service.b.todo.controller;

import org.service.b.todo.dto.DescriptionDto;
import org.service.b.todo.form.DescriptionForm;
import org.service.b.todo.model.Description;
import org.service.b.todo.service.DescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RequestMapping("/todos/{todo_id}/items")
public class DescriptionRestApi {

  @Autowired
  private DescriptionService descriptionService;

  @GetMapping("/{item_id}/descriptions")
  public ResponseEntity getDescription(@RequestParam("item_id") Long item_id) {
    List<DescriptionDto> descriptionDtos = descriptionService.getDescriptionsByItemId(item_id);
    return new ResponseEntity(descriptionDtos, HttpStatus.OK);
  }

  @PostMapping("/{item_id}/descriptions/create")
  public ResponseEntity createDescription(@RequestBody DescriptionForm descriptionForm, @RequestParam("item_id") Long item_id) {
    DescriptionDto descriptionDto = descriptionService.createDescription(descriptionForm, item_id);
    return new ResponseEntity(descriptionDto, HttpStatus.OK);
  }

}
