package gov.energy.nbc.car.servlet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletUtilities {

    public static void printToResponse(HttpServletResponse response, String message) throws IOException {

        PrintWriter writer = response.getWriter();
        writer.println(message);
        writer.flush();
    }
}
