package gov.energy.nrel.dataRepositoryApp.servletFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeadersSecurityFilter implements Filter {

    public HeadersSecurityFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // This is to remove the header indicating the server type.
        response.setHeader("Server", "");
        response.setHeader("Cache-Control", "");
        response.setHeader("Expires", "0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("X-Application-Context", "");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        // to prevent cross-origin resource sharing
        response.setHeader("Access-Control-Allow-Origin", "*");

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
