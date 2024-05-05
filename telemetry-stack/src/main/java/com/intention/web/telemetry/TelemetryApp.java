package com.intention.web.telemetry

--intenton-z2a.web.telemetry;

import com.xn--intenton-z2a.web.constructs.SimpleStackProps;
--intenton-z2a.web.utils.WebConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awscdk.App;

public class TelemetryApp {

    private static final Logger logger = LogManager.getLogger(TelemetryApp.class);

    private final static String env =
            System.getenv(WebConstants.ACCOUNT_ENV) != null
                    ? System.getenv(WebConstants.ACCOUNT_ENV)
                    : WebConstants.defaultEnv ;

    public static void main(final String[] args) {
        var stackProps = SimpleStackProps.Builder.create(TelemetryStack.class).build();
        logger.info("Created properties for stack %s".formatted(stackProps.getStackName()));
        App app = new App();
        new TelemetryStack(app, stackProps.getStackName(), stackProps);

        logger.info("Beginning CloudFormation synthesis");
        app.synth();
    }
}

