package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessService.dto.FileAsRawBytes;
import gov.energy.nbc.car.businessService.dto.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataFileDAO {

    protected Settings settings;
    private static long mostRecentTimestampUsed;

    public DataFileDAO(Settings settings) {

        this.settings = settings;
    }

    public StoredFile saveFile(MultipartFile multipartFile)
            throws CouldNoCreateDirectory, IOException {

        String dataFilesDirectoryPath = getRootDirectoryForDataFiles();
        String originalFileName = multipartFile.getOriginalFilename();

        // DESIGN NOTE: This is to prevent the files being saved during the same millisecond. This is significant
        //              because the file name will contain a time stamp to assure it's uniqueness.
        Date timestamp = getATimeStampThatIsGuaranteedToBeAfterTheOneLastUsed();

        String filenameWithEmbeddedTimestamp = constructFileName(timestamp, originalFileName);

        if (seeToItThatTheDirectoryExists(dataFilesDirectoryPath) == false) {
            throw new CouldNoCreateDirectory();
        }

        File savedFile = saveTo(multipartFile, dataFilesDirectoryPath + filenameWithEmbeddedTimestamp);

        StoredFile storedFile = new StoredFile(originalFileName, filenameWithEmbeddedTimestamp);

        return storedFile;
    }

    public StoredFile saveFile(FileAsRawBytes file)
            throws CouldNoCreateDirectory, IOException {

        String originalFileName = file.fileName;

        String rootDirectoryForDataFiles = getRootDirectoryForDataFiles();

        // DESIGN NOTE: This is to prevent the files being saved during the same millisecond. This is significant
        //              because the file name will contain a time stamp to assure it's uniqueness.
        Date timestamp = getATimeStampThatIsGuaranteedToBeAfterTheOneLastUsed();

        // DESIGN NOTE: The location is hierarchical by year and month. The idea is to avoid having a single directory
        //              containing hoards of files.
        String relativeFileLocation = constructRelativeFileLocation(timestamp, originalFileName);
        String fileName = constructFileName(timestamp, originalFileName);

        String fullyQualifiedFileLocation = rootDirectoryForDataFiles + relativeFileLocation;

        if (seeToItThatTheDirectoryExists(fullyQualifiedFileLocation) == false) {
            throw new CouldNoCreateDirectory();
        }

        File savedFile = saveTo(file.bytes, fullyQualifiedFileLocation + fileName);

        StoredFile storedFile = new StoredFile(originalFileName, relativeFileLocation + fileName);

        return storedFile;
    }

    protected String getRootDirectoryForDataFiles() {

        String dataFilesDirectoryPath = settings.getRootDirectoryForDataFiles();
        dataFilesDirectoryPath = seeToItThatItEndsWithAFileSeparator(dataFilesDirectoryPath);
        return dataFilesDirectoryPath;
    }

    protected File saveTo(MultipartFile multipartFile, String newFilePath) throws IOException {

        File newFile = new File(newFilePath);
        multipartFile.transferTo(newFile);
        return newFile;
    }

    protected File saveTo(byte[] fileContent, String newFilePath) throws IOException {

        Files.write(Paths.get(newFilePath), fileContent);
        return new File(newFilePath);
    }

    protected String seeToItThatItEndsWithAFileSeparator(String directoryToStoreTheFileIn) {

        if (directoryToStoreTheFileIn.endsWith(File.separator) == false) {
            directoryToStoreTheFileIn = directoryToStoreTheFileIn + "/";
        }
        return directoryToStoreTheFileIn;
    }

    protected String constructFileNameWithUUID(String fileName) {

        return UUID.randomUUID() + "_" + fileName;
    }

    protected static String constructRelativeFileLocation(Date timestamp, String originalFileName) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/");
        return formatter.format(timestamp);
    }

    protected static String constructFileName(Date timestamp, String originalFileName) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_aaa_hh-mm-ss_SSS-Z");
        return formatter.format(timestamp) + "_" + originalFileName;
    }

    protected static boolean seeToItThatTheDirectoryExists(String uploadPath) {

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            return uploadDir.mkdirs();
        }

        return true;
    }

    public void deletFile(String file)
            throws UnableToDeleteFile {

        boolean success = (new File(file).delete());
        if (success == false) {
            throw new UnableToDeleteFile(file);
        }
    }

    public File getFile(String storageLocation) {

        return new File(getRootDirectoryForDataFiles() + storageLocation);
    }

    protected Date getATimeStampThatIsGuaranteedToBeAfterTheOneLastUsed() {

        Date timestamp = new Date();

        if (timestamp.getTime() == this.mostRecentTimestampUsed) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
            }

            timestamp = new Date();
        }

        this.mostRecentTimestampUsed = timestamp.getTime();
        return timestamp;
    }
}
