package gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.bo.mongodb.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.singleCellSchemaApproach.s_RowDAO;
import org.apache.log4j.Logger;

public class s_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public s_RowBO(Settings settings, Settings settings_forUnitTestingPurposes) {
        super(settings, settings_forUnitTestingPurposes);
    }

    public void init(Settings settings, Settings settings_forUnitTestingPurposes) {

        rowDAO = new s_RowDAO(settings);
        rowDAO_FOR_TESTING_PURPOSES = new s_RowDAO(settings_forUnitTestingPurposes);
    }
}
