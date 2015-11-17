package gov.energy.nbc.car.app;


import gov.energy.nbc.car.bo.IBusinessObjects;
import gov.energy.nbc.car.settings.ISettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class AppSingleton {

    @Autowired
    private ISettings settings;

    private AbsAppConfig appConfig;

    @PostConstruct
    protected void init() {
        appConfig = new s_AppConfig(settings);
    }

    public AbsAppConfig getAppConfig() {
        return appConfig;
    }

    public ISettings getSettings() {
        return settings;
    }

    public IBusinessObjects getBusinessObjects(TestMode testMode) {

        return getAppConfig().getBusinessObjects(testMode);
    }
}
