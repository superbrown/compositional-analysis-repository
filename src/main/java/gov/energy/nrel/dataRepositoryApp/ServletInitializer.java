package gov.energy.nrel.dataRepositoryApp;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

// THIS CLASS IS ONLY PERTINENT IF THE APP IS BUILT AS A SPRING BOOT EXECUTABLE.

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		return application.sources(StandAloneApplication.class);
	}
}
