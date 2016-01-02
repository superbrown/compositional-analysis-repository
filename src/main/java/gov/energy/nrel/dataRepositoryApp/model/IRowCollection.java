package gov.energy.nrel.dataRepositoryApp.model;

import java.util.ArrayList;
import java.util.Set;


public interface IRowCollection {

    Set getColumnNames();

    ArrayList<IRow> getRows();
}
