package gov.energy.nrel.dataRepositoryApp.context;

import org.apache.catalina.connector.Connector;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.TimeUnit;

/**
 * This class is here to avoid a memory leak that would otherwise occur when shutting down the app.  A description of
 * the solution can be found here:
 *
 * https://github.com/spring-projects/spring-boot/issues/4657
 *
 * Before this code was in place, the following log entry could be found in the app server's log file upon app shutdown
 * ([TOMCAT_HOME]/logs/catalina.YYYY-MM-DD.log):
 *
 * org.apache.catalina.loader.WebappClassLoaderBase clearReferencesThreads
 * SEVERE: The web application [/data-repository-app] appears to have started a thread named [pool-1-thread-1] but has
 * failed to stop it. This is very likely to create a memory leak.
 *
 * UPDATE:
 * This didn't actually seem to work.  At least there is no evidence onApplicationEvent() ever gets called.  So, for the
 * time being, the issue hasn't been resolved.
 *
 */
public class TomcatConnectorCustomizer_threadShutdown
            implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    protected static Logger log = Logger.getLogger(TomcatConnectorCustomizer_threadShutdown.class);

    private volatile Connector connector;

    @Override
    public void customize(Connector connector) {
        log.debug("========== customize() Connector:  " + connector);
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        log.debug("========== Event: " + event);

        this.connector.pause();

        java.util.concurrent.Executor executor = this.connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {

            try {

                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                threadPoolExecutor.shutdown();

                if (!threadPoolExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Tomcat thread pool did not shut down gracefully within "
                            + "30 seconds. Proceeding with forceful shutdown");
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
