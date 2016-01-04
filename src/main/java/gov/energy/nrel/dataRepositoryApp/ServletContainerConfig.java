package gov.energy.nrel.dataRepositoryApp;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

// THIS CLASS IS ONLY PERTINENT IF THE APP IS BUILT AS A SPRING BOOT EXECUTABLE.

@Component
public class ServletContainerConfig implements EmbeddedServletContainerCustomizer {

    public static final String CONTEXT_PATH = "/data-repository-app";

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

        // I'm guessing this code is probably not necessary.  I think it's only used for Spring Boot executables, and
        // I think, in that case, the context is set by the server.contextPath set in the properties file.  I'm leaving
        // this here just to be helpful to someone down the line if they're having trouble setting the context path.

//        container.setContextPath(CONTEXT_PATH);
    }
}