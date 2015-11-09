package gov.energy.nbc.car.businessObject.multipleCellCollectionsApproach;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessObject.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.multipleCellCollectionApproach.RowDAO_new;
import org.apache.log4j.Logger;

public class RowBO_new extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public RowBO_new(Settings settings, Settings settings_forUnitTestingPurposes) {
        super(settings, settings_forUnitTestingPurposes);
    }

    public void init(Settings settings, Settings settings_forUnitTestingPurposes) {

        rowDAO = new RowDAO_new(settings);
        rowDAO_FOR_TESTING_PURPOSES = new RowDAO_new(settings_forUnitTestingPurposes);
    }
}
