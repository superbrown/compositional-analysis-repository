package gov.energy.nrel.dataRepositoryApp.servletFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

class ParameterOverridingHttpRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> sanitizedParameters;

    public ParameterOverridingHttpRequestWrapper(ServletRequest servletRequest, Map<String, String[]> sanitizedParameters) {
        super((HttpServletRequest) servletRequest);
        this.sanitizedParameters = sanitizedParameters;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return sanitizedParameters;
    }

    @Override
    public String getParameter(String name) {
        return sanitizedParameters.get(name)[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return sanitizedParameters.get(name);
    }
}
