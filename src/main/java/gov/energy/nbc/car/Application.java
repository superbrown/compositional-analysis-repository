package gov.energy.nbc.car;

import gov.energy.nbc.car.bo.IBusinessObjects;

public class Application {

    protected static IBusinessObjects businessObjects;

    public static IBusinessObjects getBusinessObjects() {
        return businessObjects;
    }

    public static void setBusinessObjects(IBusinessObjects businessObjects) {
        Application.businessObjects = businessObjects;
    }
}
