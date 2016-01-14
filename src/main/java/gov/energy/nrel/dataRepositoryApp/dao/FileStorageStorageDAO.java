package gov.energy.nrel.dataRepositoryApp.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.bo.mongodb.AbsDatasetBO;
import gov.energy.nrel.dataRepositoryApp.dao.exception.CouldNotCreateDirectory;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.Metadata;
import gov.energy.nrel.dataRepositoryApp.model.common.mongodb.StoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.mongodb.DatasetDocument;
import gov.energy.nrel.dataRepositoryApp.settings.ISettings;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import gov.energy.nrel.dataRepositoryApp.utilities.Utilities;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FileStorageStorageDAO implements IFileStorageDAO {

    protected ISettings settings;

    private static final String NAME_OF_DIRECTORY_FOR_ACTIVE_FILES = "active";
    protected static final String NAME_OF_DIRECTORY_FOR_REMOVED_FILES = "removed";

    public FileStorageStorageDAO(ISettings settings) {

        this.settings = settings;
    }

    @Override
    public IStoredFile saveFile(Date timestamp, String subdirectory, FileAsRawBytes fileAsRawBytes)
            throws CouldNotCreateDirectory, IOException {

        String fileName = fileAsRawBytes.fileName;

        String rootDirectoryForActiveUploadedDataFiles = getRootDirectoryForActiveUploadedDataFiles();

        // DESIGN NOTE: The location is hierarchical by year, month and time. The idea is to avoid having a single
        // directory containing hoards of files.
        String relativeFileLocation = constructRelativeFileLocation(timestamp);
        if (StringUtils.isNotBlank(subdirectory)) {
            relativeFileLocation += "/" + subdirectory;
        }

        String fullyQualifiedFileLocation = rootDirectoryForActiveUploadedDataFiles + relativeFileLocation;

        if (Utilities.assureTheDirectoryExists(fullyQualifiedFileLocation) == false) {
            throw new CouldNotCreateDirectory(fullyQualifiedFileLocation);
        }

        File savedFile = Utilities.saveFile(fileAsRawBytes.bytes, fullyQualifiedFileLocation + "/" + fileName);

        IStoredFile storedFile = new StoredFile(fileName, relativeFileLocation + "/" + fileName);

        return storedFile;
    }

    @Override
    public void moveFilesToRemovedFilesLocation(String filePath)
            throws IOException {

        String rootDirectoryForActiveUploadedDataFiles = getRootDirectoryForActiveUploadedDataFiles();
        String rootDirectoryForRemovedFiles = getRootDirectoryForRemovedFiles();

        String relativePath = extractPathToContainingDirectory(filePath);

        String sourcePath = rootDirectoryForActiveUploadedDataFiles + relativePath;
        String destinationPath = rootDirectoryForRemovedFiles + relativePath;

        Utilities.assureTheDirectoryExists(extractPathToContainingDirectory(destinationPath));

        Utilities.moveFolder(sourcePath, destinationPath);
    }

    @Override
    public void deleteFolder(String relativePath)
            throws IOException {

        String rootDirectoryForActiveUploadedDataFiles = getRootDirectoryForActiveUploadedDataFiles();
        String sourcePath = rootDirectoryForActiveUploadedDataFiles + relativePath;

        Utilities.deleteFolder(sourcePath);
    }

    private String extractPathToContainingDirectory(String filePath) {

        return filePath.substring(0, filePath.lastIndexOf("/"));
    }

    @Override
    public File getFile(String storageLocation) {

        return new File(getRootDirectoryForActiveUploadedDataFiles() + storageLocation);
    }

    protected String getRootDirectoryForActiveUploadedDataFiles() {

        return seeToItThatItEndsWithAFileSeparator(
                getRootDirectoryForUploadedDataFiles() + NAME_OF_DIRECTORY_FOR_ACTIVE_FILES);
    }

    private String getRootDirectoryForUploadedDataFiles() {

        String dataFilesDirectoryPath = settings.getRootDirectoryForUploadedDataFiles();
        return seeToItThatItEndsWithAFileSeparator(dataFilesDirectoryPath);
    }

    protected String getRootDirectoryForRemovedFiles() {

        return seeToItThatItEndsWithAFileSeparator(
                getRootDirectoryForUploadedDataFiles() + NAME_OF_DIRECTORY_FOR_REMOVED_FILES + "/");
    }

    protected String seeToItThatItEndsWithAFileSeparator(String path) {

        if (path.endsWith(File.separator) == false) {
            path = path + "/";
        }
        return path;
    }

    protected static String constructRelativeFileLocation(Date timestamp) {

        return Utilities.toString(timestamp, "yyyy/MM/yyyy-MM-dd_aaa-hh-mm-ss_SSS-Z");
    }

    @Override
    public List<Metadata> getAllMetadataForActiveData() {

        String rootDirectoryForActiveUploadedDataFiles = getRootDirectoryForActiveUploadedDataFiles();

        Utilities.assureTheDirectoryExists(rootDirectoryForActiveUploadedDataFiles);

        File rootDirectory = new File(rootDirectoryForActiveUploadedDataFiles);

        if (rootDirectory.isDirectory() == false) {throw new RuntimeException(
                rootDirectoryForActiveUploadedDataFiles + " is not a directory.");
        }

        return getAllMetadata(rootDirectory);
    }

    protected List<Metadata> getAllMetadata(File rootDirectory) {

        List<Metadata> metadataList = new ArrayList<>();

        File[] files = rootDirectory.listFiles();

        for (File file : files) {

            if (file.isDirectory()) {

                metadataList.addAll(getAllMetadata(file));
            }
            else if (file.getName().equals(AbsDatasetBO.METADATA_FILE_NAME)) {

                try {
                    String fileContents = readFile(file.getPath().toString());
                    BasicDBObject document = (BasicDBObject) JSON.parse(fileContents);
                    BasicDBObject metadataDocument = (BasicDBObject) document.get(DatasetDocument.MONGO_KEY__METADATA);

                    Metadata metadata = new Metadata(JSON.serialize(metadataDocument));
                    metadataList.add(metadata);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return metadataList;
    }


    static String readFile(String path)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.defaultCharset());
    }
}
