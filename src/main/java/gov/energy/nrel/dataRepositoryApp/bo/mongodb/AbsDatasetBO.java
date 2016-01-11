package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IPhysicalFileBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.ArchiveFailure;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.utilities.FileAsRawBytes;
import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class AbsDatasetBO extends AbsBO implements IDatasetBO {

    protected IDatasetDAO datasetDAO;

    Logger log = Logger.getLogger(this.getClass());

    public AbsDatasetBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    public File getSourceDocument(String datasetId) {

        IDatasetDocument dataset = getDatasetDAO().getDataset(datasetId);
        String storageLocation = dataset.getMetadata().getSourceDocument().getStorageLocation();
        File sourceDocument = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO().getFile(storageLocation);
        return sourceDocument;
    }

     @Override
    public ByteArrayInputStream packageAttachmentsInAZipFile(String datasetId) throws IOException {

        IDatasetDocument dataset = getDatasetDAO().getDataset(datasetId);

        byte[] byteArray;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        try {

            try {

                byteArrayOutputStream = new ByteArrayOutputStream();
                zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

                List<IStoredFile> attachments = dataset.getMetadata().getAttachments();

                for (IStoredFile attachment : attachments) {

                    String storageLocation = attachment.getStorageLocation();
                    File attachmentFile = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO().getFile(storageLocation);

                    addToZipFile(zipOutputStream, attachmentFile, attachment.getOriginalFileName());
                }
            }
            finally {

                try {

                    if (zipOutputStream != null) {
                        zipOutputStream.close();
                    }
                }
                catch (IOException e) {
                    log.warn(e);
                }
            }

            // DESIGN NOTE: It is critical that the zipOutputStream be closed before attempting to get the byte array
            //              out of the byteArrayOutputStream.  If you don't, you'll find the zip file you get is
            //              corrupted.

            byteArray = byteArrayOutputStream.toByteArray();
        }
        finally {

            try {

                if (byteArrayOutputStream != null) {
                    byteArrayOutputStream.close();
                }
            }
            catch (IOException e) {
                log.warn(e);
            }
        }

        return new ByteArrayInputStream(byteArray);
    }

    private void addToZipFile(ZipOutputStream zipOutputStream, File file, String originalFileName) throws IOException {

        ZipEntry zipEntry = new ZipEntry(originalFileName);
        zipOutputStream.putNextEntry(zipEntry);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
        }

        zipOutputStream.closeEntry();
    }

    public IDatasetDAO getDatasetDAO() {
        return datasetDAO;
    }

    public IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(ObjectId datasetId)
            throws ArchiveFailure, UnknownDataset {

        return removeDatasetFromDatabaseAndMoveItsFiles(datasetId.toHexString());
    }

    public IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(String datasetId)
            throws UnknownDataset {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        IDeleteResults results = removeDatasetFromTheDatabase(datasetId);

        moveFilesToMovedFilesDirectory(datasetDocument);

        return results;
    }

    @Override
    public IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(ObjectId datasetId)
            throws UnknownDataset {

        return removeDatasetFromDatabaseAndDeleteItsFiles(datasetId.toHexString());
    }

    @Override
    public IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(String datasetId)
            throws UnknownDataset {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        IDeleteResults results = removeDatasetFromTheDatabase(datasetId);

        deleteFiles(datasetDocument);

        return results;
    }

    public IDeleteResults removeDatasetFromTheDatabase(ObjectId datasetId)
            throws UnknownDataset {

        return removeDatasetFromTheDatabase(datasetId.toHexString());
    }

    @Override
    public IDeleteResults removeDatasetFromTheDatabase(String datasetId)
            throws UnknownDataset {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        try {
            return datasetDAO.delete(datasetId);
        }
        catch (UnknownEntity e) {
            // this is odd
            log.warn("This is odd.  We just attempted to delete dataset " + datasetId + " and it doesn't exists.  " +
                    "However, we just retreived it a few lines earlier");
        }

        return null;
    }

    public void moveFilesToMovedFilesDirectory(IDatasetDocument datasetDocument) {

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();

        try {
            IPhysicalFileBO physicalFileBO = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO();
            physicalFileBO.moveFilesToRemovedFilesLocation(storageLocation);
        }
        catch (IOException e) {
            log.warn(e);
        }
    }

    @Override
    public void deleteFiles(IDatasetDocument datasetDocument) {

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();
        String path = storageLocation.substring(0, storageLocation.lastIndexOf("/"));

        try {
            IPhysicalFileBO physicalFileBO = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO();
            physicalFileBO.deleteFolder(path);
        }
        catch (IOException e) {
            log.warn(e);
        }
    }

    protected void saveMetadataAsJsonFile(Date timestamp, ObjectId objectId) {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(objectId.toHexString());
        byte[] json = JSON.serialize(datasetDocument).getBytes();

        FileAsRawBytes jsonAsRawBytes = new FileAsRawBytes("DATASET_METADATA.json", json);

        IPhysicalFileBO physicalFileBO = getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO();
        physicalFileBO.saveFile(timestamp, "", jsonAsRawBytes);
    }

    public String getAllDatasets() {

        Iterable<Document> datasets = getDatasetDAO().getAll();

        String jsonOut = DAOUtilities.serialize(datasets);
        return jsonOut;
    }

    public String getDataset(String datasetId)
            throws UnknownDataset {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        String jsonOut = DAOUtilities.serialize(datasetDocument);
        return jsonOut;
    }

    protected File getPhysicalFile(String storageLocation) {

        return getDataRepositoryApplication().getBusinessObjects().getPhysicalFileBO().getFile(storageLocation);
    }
}
