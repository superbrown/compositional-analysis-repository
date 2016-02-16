package gov.energy.nrel.dataRepositoryApp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.writers.DuplicateValuePreventingStaticHeadersWriter;

@EnableWebSecurity
@Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        HeadersConfigurer<HttpSecurity> securityHeadersConfigurer = http.headers();

        // NOTE: The reason we're not doing this is because it writes duplicate header values when the app redirects
        // the 404 error page.  Spring Boot must be chaining things together.  To get around this, I've created a
        // class called DuplicateValuePreventingStaticHeadersWriter that will prevent duplicate header values.  I am
        // using it in the code below, which adds the same headers explicitly that these other calls added implicitly.

//        securityHeadersConfigurer
//                .cacheControl()
//                .frameOptions()
//                .xssProtection()
//                .addHeaderWriter(new StaticHeadersWriter("Server",""))

        securityHeadersConfigurer
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Server", ""))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Cache-Control", ""))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Expires", "0"))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Pragma", "no-cache"))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("X-Application-Context", ""))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("X-Frame-Options", "DENY"))
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("X-XSS-Protection", "1; mode=block"));

        // This is to remove the header indicating the server type.
        securityHeadersConfigurer
                .addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Server", ""));

        // to prevent cross-origin resource sharing
        securityHeadersConfigurer.
            addHeaderWriter(new DuplicateValuePreventingStaticHeadersWriter("Access-Control-Allow-Origin", "*"));
    }
}