package gov.energy.nrel.dataRepositoryApp.dao.mongodb.dto;

import com.mongodb.client.result.DeleteResult;
import gov.energy.nrel.dataRepositoryApp.dao.dto.IDeleteResults;

import java.util.ArrayList;
import java.util.List;

public class DeleteResults extends DeleteResult implements IDeleteResults {

    protected List<DeleteResult> deleteResults = new ArrayList<>();


    public DeleteResults() {
    }

    public DeleteResults(DeleteResult deleteResult) {

        this.deleteResults.add(deleteResult);
    }

    @Override
    public boolean wasAcknowledged() {

        for (DeleteResult deleteResult : deleteResults) {

             if (deleteResult.wasAcknowledged() == false) {

                 return false;
             }
        }

        return true;
    }

    @Override
    public long getDeletedCount() {

        long deleteCount = 0;

        for (DeleteResult deleteResult : deleteResults) {

            deleteCount += deleteResult.getDeletedCount();
        }

        return deleteCount;
    }

    @Override
    public void addAll(IDeleteResults deleteResults) {

        this.addAll((DeleteResults)deleteResults);
    }

    public void add(DeleteResult deleteResult) {

        deleteResults.add(deleteResult);
    }

    public void addAll(List<DeleteResult> deleteResults) {

        this.deleteResults.addAll(deleteResults);
    }

    public List<DeleteResult> getDeleteResults() {

        return deleteResults;
    }

    public void addAll(DeleteResults deleteResultsForRows) {

        addAll(deleteResultsForRows.getDeleteResults());
    }
}
