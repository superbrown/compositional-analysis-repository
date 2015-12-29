package gov.energy.nbc.car.bo.mongodb;

import gov.energy.nbc.car.bo.IDatasetBO;
import gov.energy.nbc.car.bo.IPhysicalFileBO;
import gov.energy.nbc.car.bo.FileStorageBO;
import gov.energy.nbc.car.dao.IDatasetDAO;
import gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach.s_DatasetDAO;
import gov.energy.nbc.car.model.IDatasetDocument;
import gov.energy.nbc.car.model.IStoredFile;
import gov.energy.nbc.car.settings.ISettings;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class AbsDatasetBO implements IDatasetBO {

    Logger log = Logger.getLogger(this.getClass());

    protected IDatasetDAO datasetDAO;
    protected IPhysicalFileBO physicalFileBO;

    public AbsDatasetBO(ISettings settings) {

        physicalFileBO = new FileStorageBO(settings);
        datasetDAO = new s_DatasetDAO(settings);
    }

    public IDatasetDAO getDatasetDAO() {
        return datasetDAO;
    }

    public File getSourceDocument(String datasetId) {

        IDatasetDocument dataset = getDatasetDAO().getDataset(datasetId);
        String storageLocation = dataset.getMetadata().getSourceDocument().getStorageLocation();
        File sourceDocument = physicalFileBO.getFile(storageLocation);
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
                    File attachmentFile = physicalFileBO.getFile(storageLocation);

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

//        byte[] bytesInFile = Utilities.toBytes(file);
//        zipOutputStream.write(bytesInFile);

        zipOutputStream.closeEntry();
    }

}
