package org.springframework.security.web.header.writers;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.security.web.header.Header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This class exists to provide slightly different behavior from the StaticHeadersWriter class's
 * writeHeaders() method.  The StaticHeadersWriter class uses the HttpServletResponse.addHeader()
 * method to add header values rather than setHeader().  Due to that method's behavior, if a
 * value with the same name gets added more than once, that name will be present more than once
 * in the response headers, which is something we want to avoid.
 */
public class DuplicateValuePreventingStaticHeadersWriter extends StaticHeadersWriter {

	public DuplicateValuePreventingStaticHeadersWriter(String headerName, String... headerValues) {
		super(headerName, headerValues);
	}

	public DuplicateValuePreventingStaticHeadersWriter(List<Header> headers) {
		super(headers);
	}

	public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {

		for (Header header : getHeaders()) {
			for (String value : header.getValues()) {
				response.setHeader(header.getName(), value);
			}
		}
	}

	public List<Header> getHeaders() {
		// get around the fact that the headers attribute is declared private in my parent
		List<Header> headers = null;
		try {
			headers = (List<Header>) FieldUtils.readField(this, "headers", true);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return headers;
	}
}
