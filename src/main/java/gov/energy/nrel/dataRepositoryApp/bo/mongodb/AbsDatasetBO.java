package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import com.mongodb.util.JSON;
import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.bo.IFileStorageBO;
import gov.energy.nrel.dataRepositoryApp.bo.exception.FailedToDeleteFiles;
import gov.energy.nrel.dataRepositoryApp.bo.exception.UnknownDataset;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetTransactionTokenDAO;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;
import gov.energy.nrel.dataRepositoryApp.dao.exception.UnknownEntity;
import gov.energy.nrel.dataRepositoryApp.dao.mongodb.DAOUtilities;
import gov.energy.nrel.dataRepositoryApp.model.common.IStoredFile;
import gov.energy.nrel.dataRepositoryApp.model.document.IDatasetDocument;
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

    protected static Logger log = Logger.getLogger(AbsDatasetBO.class);
    protected IDatasetTransactionTokenDAO datasetTransactionTokenDAO;

    public AbsDatasetBO(DataRepositoryApplication dataRepositoryApplication) {
        super(dataRepositoryApplication);
    }

    public File getSourceDocument(String datasetId) {

        IDatasetDocument dataset = getDatasetDAO().getDataset(datasetId);
        String storageLocation = dataset.getMetadata().getSourceDocument().getStorageLocation();
        File sourceDocument = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO().getFile(storageLocation);
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
                IFileStorageBO fileSotrageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();

                for (IStoredFile attachment : attachments) {

                    String storageLocation = attachment.getStorageLocation();
                    File attachmentFile = fileSotrageBO.getFile(storageLocation);

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
                    log.warn(e, e);
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
                log.warn(e, e);
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

    public IDeleteResults removeDatasetFromDatabaseAndMoveItsFiles(String datasetId)
            throws UnknownDataset, FailedToDeleteFiles {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        IDeleteResults results = null;
        try {
            results = datasetDAO.delete(datasetId);
        }
        catch (UnknownEntity e) {
            throw new FailedToDeleteFiles(e);
        }

        moveFilesToMovedFilesDirectory(datasetDocument);

        return results;
    }

    @Override
    public IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(ObjectId datasetId)
            throws UnknownDataset, FailedToDeleteFiles {

        return removeDatasetFromDatabaseAndDeleteItsFiles(datasetId.toHexString());
    }

    @Override
    public IDeleteResults removeDatasetFromDatabaseAndDeleteItsFiles(String datasetId)
            throws UnknownDataset, FailedToDeleteFiles {

        IDatasetDAO datasetDAO = getDatasetDAO();
        IDatasetDocument datasetDocument = datasetDAO.getDataset(datasetId);

        if (datasetDocument == null) {
            throw new UnknownDataset();
        }

        IDeleteResults results = null;
        try {
            results = datasetDAO.delete(datasetId);
        }
        catch (UnknownEntity e) {
            throw new FailedToDeleteFiles(e);
        }

        deleteFiles(datasetDocument);

        return results;
    }

    public void moveFilesToMovedFilesDirectory(IDatasetDocument datasetDocument) {

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();

        try {
            IFileStorageBO fileSotrageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();
            fileSotrageBO.moveFilesToRemovedFilesLocation(storageLocation);
        }
        catch (IOException e) {
            log.warn(e, e);
        }
    }

    @Override
    public void deleteFiles(IDatasetDocument datasetDocument) throws FailedToDeleteFiles {

        String storageLocation = datasetDocument.getMetadata().getSourceDocument().getStorageLocation();
        String path = storageLocation.substring(0, storageLocation.lastIndexOf("/"));

        IFileStorageBO fileStorageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();
        try {
            fileStorageBO.deleteFolder(path);
        } catch (IOException e) {
            throw new FailedToDeleteFiles(e);
        }
    }

    protected void saveMetadataAsJsonFile(Date timestamp, ObjectId objectId) {

        IDatasetDocument datasetDocument = getDatasetDAO().getDataset(objectId.toHexString());
        byte[] json = JSON.serialize(datasetDocument).getBytes();

        FileAsRawBytes jsonAsRawBytes = new FileAsRawBytes("DATASET_METADATA.json", json);

        IFileStorageBO fileStorageBO = getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO();
        fileStorageBO.saveFile(timestamp, "", jsonAsRawBytes);
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

        return getDataRepositoryApplication().getBusinessObjects().getFileSotrageBO().getFile(storageLocation);
    }

    @Override
    public void removeDatasetTransactionToken(ObjectId datasetObjectId) {
        try {
            datasetTransactionTokenDAO.removeToken(datasetObjectId);
        }
        catch (UnknownEntity unknownEntity) {
            log.error(unknownEntity);
        }
    }

    @Override
    public List<ObjectId> getDatasetIdsForAllIncompleteDatasetUploadCleanups() {
        return datasetTransactionTokenDAO.getDatasetIdsOfAllTokens();
    }
}
