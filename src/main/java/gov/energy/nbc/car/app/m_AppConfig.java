package gov.energy.nbc.car.app;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.mongodb.multipleCellSchemaApproach.m_BusinessObjects;

public class m_AppConfig extends AbsAppConfig {

    public m_AppConfig(ISettings settings) {
        super(settings);
    }

    @Override
    protected IBusinessObjects createBusinessObjects(ISettings settings) {
        return new m_BusinessObjects(settings);
    }
}
