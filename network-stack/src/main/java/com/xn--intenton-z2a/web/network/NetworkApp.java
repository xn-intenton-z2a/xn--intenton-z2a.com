package com.xn--intenton-z2a.web.network;

import com.xn--intenton-z2a.web.constructs.SimpleStackProps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awscdk.App;
import software.amazon.awssdk.regions.Region;

public class NetworkApp {

    private static final Logger logger = LogManager.getLogger(NetworkApp.class);

    public static void main(final String[] args) {
        var stackProps = SimpleStackProps.Builder
                .create(NetworkStack.class)
                .region(Region.US_EAST_1.toString())
                .build();
        logger.info("Created properties for stack %s".formatted(stackProps.getStackName()));
        App app = new App();
        new NetworkStack(app, stackProps.getStackName(), stackProps);

        logger.info("Beginning CloudFormation synthesis");
        app.synth();
    }
}

