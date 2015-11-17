package gov.energy.nbc.car.app;

import gov.energy.nbc.car.settings.ISettings;
import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.bo.mongodb.singleCellSchemaApproach.s_BusinessObjects;

public class s_AppConfig extends AbsAppConfig {

    public s_AppConfig(ISettings settings) {
        super(settings);
    }

    @Override
    protected IBusinessObjects createBusinessObjects(ISettings settings) {
        return new s_BusinessObjects(settings);
    }
}
