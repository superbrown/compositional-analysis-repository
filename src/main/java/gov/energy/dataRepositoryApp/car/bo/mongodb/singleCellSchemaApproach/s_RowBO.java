package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach.s_RowDAO;
import org.apache.log4j.Logger;

public class s_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public s_RowBO(ISettings settings) {
        super(settings);
    }

    public void init(ISettings settings) {
        rowDAO = new s_RowDAO(settings);
    }
}
