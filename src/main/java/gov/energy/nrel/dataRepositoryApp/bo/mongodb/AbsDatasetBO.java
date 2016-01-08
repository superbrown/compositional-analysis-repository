package gov.energy.nrel.dataRepositoryApp.bo.mongodb;

import gov.energy.nrel.dataRepositoryApp.DataRepositoryApplication;
import gov.energy.nrel.dataRepositoryApp.bo.IDatasetBO;
import gov.energy.nrel.dataRepositoryApp.dao.IDatasetDAO;
import gov.energy.nrel.dataRepositoryApp.model.IDatasetDocument;
import gov.energy.nrel.dataRepositoryApp.model.IStoredFile;
import org.apache.log4j.Logger;

import java.io.*;
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
}
