package gov.energy.nrel.dataRepositoryApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.annotation.MultipartConfig;
import java.util.Arrays;

// THIS CLASS IS ONLY PERTINENT IF THE APP IS BUILT AS A SPRING BOOT EXECUTABLE.

@SpringBootApplication
@Configuration
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*50, maxRequestSize=1024*1024*5*50)
public class StandAloneApplication {

    public static void main(String[] args) {

        // This code is all here for the creation of a stand-alone Spring Boot application, and since I ended up
        // packaging the app as a war instead, I quit working with this code.

        SpringApplication.run(StandAloneApplication.class, args);

        ApplicationContext ctx = SpringApplication.run(StandAloneApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }

//        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
//        rootContext.register(ApplicationConfig.class, WebConfig.class);
//
//        servletContext.addListener(new ContextLoaderListener(rootContext));
//
//        //Spring security
//        servletContext.addFilter("springSecurityFilterChain", new DelegatingFilterProxy("springSecurityFilterChain")).addMappingForUrlPatterns(null, false, "/*");
//
//        //Enable multipart support
//        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("DispatcherServlet", new DispatcherServlet(rootContext));
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.addMapping("/");
//
//        dispatcher.setMultipartConfig(
//                new MultipartConfigElement("/tmp", 25 * MEGABYTE, 125 * MEGABYTE, 1 * MEGABYTE)
//        );
    }
}
