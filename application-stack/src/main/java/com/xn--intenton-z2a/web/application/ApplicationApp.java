package com.xn--intenton-z2a.web.application;

import com.xn--intenton-z2a.web.constructs.SimpleStackProps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awscdk.App;

public class ApplicationApp {

    private static final Logger logger = LogManager.getLogger(ApplicationApp.class);

    public static void main(final String[] args) {
        var stackProps = SimpleStackProps.Builder.create(ApplicationStack.class).build();
        logger.info("Created properties for stack %s".formatted(stackProps.getStackName()));
        App app = new App();
        new ApplicationStack(app, stackProps.getStackName(), stackProps);

        logger.info("Beginning CloudFormation synthesis");
        app.synth();
    }
}

