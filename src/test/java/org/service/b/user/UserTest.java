package org.service.b.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.service.b.ServiceBOrg;
import org.service.b.auth.model.User;
import org.service.b.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@TestPropertySource
public class UserTest {

    @Autowired
    UserRepo userRepo;

    @Test
    public void getUsers() {
        userRepo.save(new User());
    }

}
