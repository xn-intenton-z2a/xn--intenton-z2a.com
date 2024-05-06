package com.intention.web.application;

import com.intention.web.constructs.SimpleStackProps;
import com.intention.web.utils.WebConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.IOException;
import java.util.HashMap;

@ExtendWith(SystemStubsExtension.class)
public class ApplicationTest {

    private static final Logger logger = LogManager.getLogger(ApplicationApp.class);

    private static final String testAccount = "111111111111";
    private static final String testRegion = "eu-west-2";

    @SystemStub
    private EnvironmentVariables environmentVariables =
            new EnvironmentVariables(
                    WebConstants.JSII_SILENCE_WARNING_UNTESTED_NODE_VERSION, "true",
                    WebConstants.JSII_SILENCE_WARNING_DEPRECATED_NODE_VERSION, "true",
                    WebConstants.TARGET_ENV, "test",
                    WebConstants.CDK_DEFAULT_ACCOUNT, testAccount,
                    WebConstants.CDK_DEFAULT_REGION, WebConstants.defaultRegion
            );

    @Test
    public void expectLoggerToLog() throws IOException {
        Assertions.assertNotNull(logger);
        logger.debug("Logging at debug level");
        logger.info("Logging at info level");
        logger.warn("Logging at warn level");
        logger.error("Logging at error level");
    }

    @Test
    public void expectBucketToHaveEnvironmentSpecificName() throws IOException {
        var stackProps = SimpleStackProps.Builder.create(ApplicationStack.class).build();
        logger.info("Created properties for stack %s".formatted(stackProps.getStackName()));
        App app = new App();
        var stack = new ApplicationStack(app, stackProps.getStackName(), stackProps);
        Template template = Template.fromStack(stack);

        Assertions.assertEquals(testAccount, stack.getAccount());
        Assertions.assertEquals(testRegion, stack.getRegion());
        template.resourceCountIs("AWS::S3::Bucket", 3);
        template.hasResourceProperties("AWS::S3::Bucket", new HashMap<>()
            {{ put("BucketName", "%s-web-intention-com".formatted("test")); }});
    }
}
