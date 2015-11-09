package gov.energy.nbc.car.businessObject.singleCellCollectionApproach;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessObject.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.singleCellCollectionApproach.RowDAO;
import org.apache.log4j.Logger;

public class RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public RowBO(Settings settings, Settings settings_forUnitTestingPurposes) {
        super(settings, settings_forUnitTestingPurposes);
    }

    public void init(Settings settings, Settings settings_forUnitTestingPurposes) {

        rowDAO = new RowDAO(settings);
        rowDAO_FOR_TESTING_PURPOSES = new RowDAO(settings_forUnitTestingPurposes);
    }
}
