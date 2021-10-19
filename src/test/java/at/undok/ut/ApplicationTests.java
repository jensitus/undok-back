package at.undok.ut;

import at.undok.undok.client.model.entity.Client;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationTests {

    @Test
    public void contextLoads() {

        Client client = new Client();
        client.setEducation("Basis Wappler");


    }

}
