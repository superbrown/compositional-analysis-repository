package gov.energy.nrel.dataRepositoryApp.servletFilter;

import gov.energy.nrel.dataRepositoryApp.utilities.ValueScrubbingHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParameterScrubbingFilter implements Filter {

    private ValueScrubbingHelper valueScrubbingHelper;


    public ParameterScrubbingFilter(ValueScrubbingHelper valueScrubbingHelper) {

        this.valueScrubbingHelper = valueScrubbingHelper;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Map<String, String[]> parameters = request.getParameterMap();
        final Map<String, String[]> scrubbedParameters = scrubParameters(parameters);

        HttpServletRequestWrapper requestWrapper =
                new ParameterOverridingHttpRequestWrapper(servletRequest, scrubbedParameters);

        filterChain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public Map<String, String[]> scrubParameters(Map<String, String[]> parameters) {

        Map<String, String[]> scrubbedParameterMap = new HashMap<>();

        for (String key : parameters.keySet()) {

            String[] values = parameters.get(key);

            String[] scrubbedValues = new String[values.length];

            for (int i = 0; i < values.length; i++) {

                String value = values[i];
                scrubbedValues[i] = valueScrubbingHelper.scrubValue(value);
            }

            scrubbedParameterMap.put(key, scrubbedValues);
        }

        return scrubbedParameterMap;
    }
}
