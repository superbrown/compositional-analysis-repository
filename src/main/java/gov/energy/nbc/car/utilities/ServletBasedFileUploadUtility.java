package gov.energy.nbc.car.utilities;

import gov.energy.nbc.car.businessService.dto.StoredFile;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServletBasedFileUploadUtility extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int THRESHOLD_SIZE = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB


    public static List<StoredFile> saveFilesInTheRequest(
            HttpServletRequest request,
            String directory)
            throws Exception {

        directory = seeToItThatItEndsWithAFileSeparator(directory);

        List<FileItem> filesFromRequest = getFilesFromRequest(request);

        seeToItThatTheDirectoryExists(directory);

        List<StoredFile> files = new ArrayList();

        for (FileItem fileItem : filesFromRequest) {

            String originalFileName = extractFileName(fileItem);
            String fileNameWithUUID = constructFileNameWithUUID(originalFileName);

            File file = new File(directory + fileNameWithUUID);
            file.setExecutable(true,false);
            file.setReadable(true,false);
            file.setWritable(true,false);

            // save the file on disk
            fileItem.write(file);
            files.add(new StoredFile(originalFileName, fileNameWithUUID));
        }

        return files;
    }

    protected static String seeToItThatItEndsWithAFileSeparator(String directoryToStoreTheFileIn) {

        if (directoryToStoreTheFileIn.endsWith(File.separator) == false) {
            directoryToStoreTheFileIn = directoryToStoreTheFileIn + File.separator;
        }
        return directoryToStoreTheFileIn;
    }

    protected static String extractFileName(FileItem fileItem) {

        return new File(fileItem.getName()).getName();
    }

    protected static List<FileItem> getFilesFromRequest(HttpServletRequest request)
            throws ServletException, IOException, FileUploadException {

        if (ServletFileUpload.isMultipartContent(request) == false) {
            return null;
        }

        try {
            ServletFileUpload servletFileUpload = getServletFileUpload();

            ServletRequestContext servletRequestContext = new ServletRequestContext(request);
            List<FileItem> fileItems = servletFileUpload.parseRequest(servletRequestContext);

            List<FileItem> files = new ArrayList();

            for (FileItem fileItem : fileItems) {

                if (fileItem.isFormField() == false) {

                    files.add(fileItem);
                }
            }

            return files;
        }
        catch (FileUploadException e) {
            throw new RuntimeException(e);
        }
    }


    protected static String constructFileNameWithUUID(String fileName) {

        return UUID.randomUUID() + "_" + fileName;
    }

    protected static ServletFileUpload getServletFileUpload() {

        // configures upload settings
        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        diskFileItemFactory.setSizeThreshold(THRESHOLD_SIZE);
        diskFileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
        servletFileUpload.setFileSizeMax(MAX_FILE_SIZE);
        servletFileUpload.setSizeMax(MAX_REQUEST_SIZE);
        return servletFileUpload;
    }

    protected static void seeToItThatTheDirectoryExists(String uploadPath) {

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
    }
}