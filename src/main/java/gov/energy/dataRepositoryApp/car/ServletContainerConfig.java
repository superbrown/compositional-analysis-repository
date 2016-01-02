package gov.energy.nbc.car;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

@Component
public class ServletContainerConfig implements EmbeddedServletContainerCustomizer {

    public static final String CONTEXT_PATH = "/data-repository-app";

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {

        container.setContextPath(CONTEXT_PATH);
    }
}