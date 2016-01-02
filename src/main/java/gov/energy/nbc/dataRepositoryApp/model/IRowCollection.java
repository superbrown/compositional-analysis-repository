package gov.energy.nbc.car.model;

import java.util.ArrayList;
import java.util.Set;


public interface IRowCollection {

    Set getColumnNames();

    ArrayList<IRow> getRows();
}
