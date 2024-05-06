package com.intention.web.constructs;

import com.intention.web.utils.WebConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class LogForwardingBucketTest {

    private static final String testAccount = "111111111111";

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
    public void testLogForwardingBucket() {
        var stackProps = SimpleStackProps.Builder.create(LogForwardingBucket.class).build();
        App app = new App();
        var stack = new LogForwardingBucket(app, stackProps.getStackName(), stackProps);
        Template template = Template.fromStack(stack);
        Assertions.assertNotNull(template);
        template.resourceCountIs("AWS::S3::Bucket", 1);
    }
}
