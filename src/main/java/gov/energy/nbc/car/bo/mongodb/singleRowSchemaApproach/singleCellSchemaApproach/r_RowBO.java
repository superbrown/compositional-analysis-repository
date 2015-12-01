package gov.energy.nbc.car.bo.mongodb.singleRowSchemaApproach.singleCellSchemaApproach;

import gov.energy.nbc.car.bo.mongodb.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.everthingInTheRowCollectionApproach.r_RowDAO;
import gov.energy.nbc.car.settings.ISettings;
import org.apache.log4j.Logger;

public class r_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public r_RowBO(ISettings settings) {
        super(settings);
    }

    public void init(ISettings settings) {
        rowDAO = new r_RowDAO(settings);
    }
}
