package org.miro.test.widgets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class WidgetsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(WidgetsApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "4200"));
		app.run(args);
	}

}
