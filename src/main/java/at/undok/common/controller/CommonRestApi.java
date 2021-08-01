package at.undok.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8080", "https://undok.herokuapp.com"}, maxAge = 3600)
@RestController
@RequestMapping("/service/app")
public class CommonRestApi {

}
