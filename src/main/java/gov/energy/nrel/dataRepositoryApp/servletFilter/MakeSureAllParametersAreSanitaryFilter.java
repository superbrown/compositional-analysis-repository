package gov.energy.nrel.dataRepositoryApp.servletFilter;

import gov.energy.nrel.dataRepositoryApp.utilities.valueSanitizer.IValueSanitizer;
import gov.energy.nrel.dataRepositoryApp.utilities.fileReader.UnsanitaryRequestParameter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class MakeSureAllParametersAreSanitaryFilter implements Filter {

    private IValueSanitizer valueSanitizer;


    public MakeSureAllParametersAreSanitaryFilter(IValueSanitizer valueSanitizer) {

        this.valueSanitizer = valueSanitizer;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Map<String, String[]> parameters = request.getParameterMap();
        try {
            throwExceptionIfAnyValuesAreUnanitary(parameters);
        }
        catch (UnsanitaryRequestParameter e) {

            HttpServletResponse response = (HttpServletResponse) servletResponse;

            org.springframework.http.HttpStatus httpStatus =
                    org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

            response.setContentType("application/json");
            response.setStatus(httpStatus.value());
            response.getWriter().println(
                    "{ " +
                    "\"status\":\"" + httpStatus.value() + "\", " +
                    "\"reasonPhrase\":\"" + httpStatus.getReasonPhrase() + "\", " +
                    "\"message\":\"The parameter, " + e.paramaterName + ", contains a value that could potentially be malicious.\" " +
                    "}");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    public void throwExceptionIfAnyValuesAreUnanitary(Map<String, String[]> parameters) throws UnsanitaryRequestParameter {

        for (String key : parameters.keySet()) {

            String[] values = parameters.get(key);

            for (int i = 0; i < values.length; i++) {

                String value = values[i];
                String sanitizedValue = valueSanitizer.sanitize(value);

                if (sanitizedValue.equals(value) != true) {

                    throw new UnsanitaryRequestParameter(key);
                }
            }
        }
    }
}
