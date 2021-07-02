package at.undok;

import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.enumeration.LabourMarketAccess;
import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.enumeration.ResidentStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {

		Client client = new Client();
		client.setEducation("Basis Wappler");


	}

}
