package gov.energy.nbc.car.dao;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.dao.dto.FileAsRawBytes;
import gov.energy.nbc.car.dao.dto.StoredFile;
import gov.energy.nbc.car.dao.exception.CouldNoCreateDirectory;
import gov.energy.nbc.car.dao.exception.UnableToDeleteFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileStorageStorageDAO implements IFileStorageDAO {

    protected ISettings settings;

    protected static final String NAME_OF_DIRECTORY_FOR_REMOVED_FILES = "removed";

    public FileStorageStorageDAO(ISettings settings) {

        this.settings = settings;
    }

    @Override
    public StoredFile saveFile(Date timestamp, FileAsRawBytes file)
            throws CouldNoCreateDirectory, IOException {

        String fileName = file.fileName;

        String rootDirectoryForDataFiles = getRootDirectoryForDataFiles();

        // DESIGN NOTE: The location is hierarchical by year, month and time. The idea is to avoid having a single
        // directory containing hoards of files.
        String relativeFileLocation = constructRelativeFileLocation(timestamp);

        String fullyQualifiedFileLocation = rootDirectoryForDataFiles + relativeFileLocation;

        if (seeToItThatTheDirectoryExists(fullyQualifiedFileLocation) == false) {
            throw new CouldNoCreateDirectory();
        }

        File savedFile = saveTo(file.bytes, fullyQualifiedFileLocation + "/" + fileName);

        StoredFile storedFile = new StoredFile(fileName, relativeFileLocation + "/" + fileName);

        return storedFile;
    }


    @Override
    public void deletFile(String file)
            throws UnableToDeleteFile {

        boolean success = (new File(getRootDirectoryForDataFiles() + file).delete());
        if (success == false) {
            throw new UnableToDeleteFile(file);
        }
    }

    @Override
    public void moveFilesToRemovedFilesLocation(String filePath)
            throws IOException {

        String rootDirectoryForRemovedFiles = getRootDirectoryForRemovedFiles();

        seeToItThatTheDirectoryExists(rootDirectoryForRemovedFiles);

        String pathToContainingDirectory =
                getRootDirectoryForDataFiles() +
                extractPathToContainingDirectory(filePath);

        Files.move(
                Paths.get(pathToContainingDirectory),
                Paths.get(rootDirectoryForRemovedFiles),
                StandardCopyOption.ATOMIC_MOVE);
    }

    private String extractPathToContainingDirectory(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf("/"));
    }

    @Override
    public File getFile(String storageLocation) {

        return new File(getRootDirectoryForDataFiles() + storageLocation);
    }

    protected String getRootDirectoryForDataFiles() {

        String dataFilesDirectoryPath = settings.getRootDirectoryForUploadedDataFiles();
        dataFilesDirectoryPath = seeToItThatItEndsWithAFileSeparator(dataFilesDirectoryPath);
        return dataFilesDirectoryPath;
    }

    protected String getRootDirectoryForRemovedFiles() {

        return getRootDirectoryForDataFiles() + NAME_OF_DIRECTORY_FOR_REMOVED_FILES + "/";
    }

    protected File saveTo(byte[] fileContent, String newFilePath) throws IOException {

        Files.write(Paths.get(newFilePath), fileContent);
        return new File(newFilePath);
    }

    protected String seeToItThatItEndsWithAFileSeparator(String path) {

        if (path.endsWith(File.separator) == false) {
            path = path + "/";
        }
        return path;
    }

    protected static String constructRelativeFileLocation(Date timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/yyyy-MM-dd_aaa-hh-mm-ss_SSS-Z");
        return formatter.format(timestamp);
    }

    protected static boolean seeToItThatTheDirectoryExists(String uploadPath) {

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            return uploadDir.mkdirs();
        }

        return true;
    }
}
