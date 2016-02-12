package gov.energy.nrel.dataRepositoryApp.servletFilter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

class ParameterOverridingHttpRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> scrubbedParameters;

    public ParameterOverridingHttpRequestWrapper(ServletRequest servletRequest, Map<String, String[]> scrubbedParameters) {
        super((HttpServletRequest) servletRequest);
        this.scrubbedParameters = scrubbedParameters;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return scrubbedParameters;
    }

    @Override
    public String getParameter(String name) {
        return scrubbedParameters.get(name)[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        return scrubbedParameters.get(name);
    }
}
