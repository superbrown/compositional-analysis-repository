package gov.energy.nbc.car.businessObject.multipleCellSchemaApproach;

import gov.energy.nbc.car.Settings;
import gov.energy.nbc.car.businessObject.AbsRowBO;
import gov.energy.nbc.car.dao.mongodb.multipleCellSchemaApproach.m_RowDAO;
import org.apache.log4j.Logger;

public class m_RowBO extends AbsRowBO {

    protected Logger log = Logger.getLogger(getClass());

    public m_RowBO(Settings settings, Settings settings_forUnitTestingPurposes) {
        super(settings, settings_forUnitTestingPurposes);
    }

    public void init(Settings settings, Settings settings_forUnitTestingPurposes) {

        rowDAO = new m_RowDAO(settings);
        rowDAO_FOR_TESTING_PURPOSES = new m_RowDAO(settings_forUnitTestingPurposes);
    }
}
