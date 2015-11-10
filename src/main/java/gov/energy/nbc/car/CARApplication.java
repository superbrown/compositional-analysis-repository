package gov.energy.nbc.car;

import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.annotation.MultipartConfig;
import java.util.Arrays;

@SpringBootApplication
@Configuration
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*50, maxRequestSize=1024*1024*5*50)
public class CARApplication {

    public static void main(String[] args) {

        Application.setBusinessObjects(new m_BusinessObjects(new Settings(), new Settings_forUnitTestPurposes()));

        SpringApplication.run(CARApplication.class, args);

        ApplicationContext ctx = SpringApplication.run(CARApplication.class, args);

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
