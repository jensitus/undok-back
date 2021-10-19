package at.undok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

@SpringBootApplication
public class UndokAt {

	public static void main(String[] args) {
		var application = new SpringApplication(UndokAt.class);
		setDefaultProfileIfNoOverridesExist(application);
		application.run(args);
	}

	private static void setDefaultProfileIfNoOverridesExist(SpringApplication application) {
		var properties = new HashMap<String, Object>();
		properties.put("spring.profiles.default", "dev");
		application.setDefaultProperties(properties);
	}

}
