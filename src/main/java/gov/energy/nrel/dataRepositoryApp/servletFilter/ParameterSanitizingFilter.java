package gov.energy.nrel.dataRepositoryApp.servletFilter;

import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParameterSanitizingFilter implements Filter {

    private IValueSanitizer valueSanitizer;


    public ParameterSanitizingFilter(IValueSanitizer valueSanitizer) {

        this.valueSanitizer = valueSanitizer;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Map<String, String[]> parameters = request.getParameterMap();
        final Map<String, String[]> sanitizedParameters = sanitizeParameters(parameters);

        HttpServletRequestWrapper requestWrapper =
                new ParameterOverridingHttpRequestWrapper(servletRequest, sanitizedParameters);

        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public Map<String, String[]> sanitizeParameters(Map<String, String[]> parameters) {

        Map<String, String[]> sanitizedParameterMap = new HashMap<>();

        for (String key : parameters.keySet()) {

            String[] values = parameters.get(key);

            String[] sanitizedValue = new String[values.length];

            for (int i = 0; i < values.length; i++) {

                String value = values[i];
                sanitizedValue[i] = valueSanitizer.sanitize(value);
            }

            sanitizedParameterMap.put(key, sanitizedValue);
        }

        return sanitizedParameterMap;
    }
}
