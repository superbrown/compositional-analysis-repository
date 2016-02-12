package gov.energy.nrel.dataRepositoryApp.servletFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

class ParameterOverridingHttpRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> scrubParameters;

    public ParameterOverridingHttpRequestWrapper(ServletRequest servletRequest, Map<String, String[]> scrubParameters) {
        super((HttpServletRequest) servletRequest);
        this.scrubParameters = scrubParameters;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return scrubParameters;
    }

    @Override
    public String getParameter(String name) {
        return scrubParameters.get(name)[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return scrubParameters.get(name);
    }
}
