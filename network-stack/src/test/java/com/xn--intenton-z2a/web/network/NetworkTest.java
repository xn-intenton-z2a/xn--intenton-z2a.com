package com.xn--intenton-z2a.web.network;

import com.xn--intenton-z2a.web.constructs.SimpleStackProps;
import com.xn--intenton-z2a.web.utils.WebConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.IOException;

@ExtendWith(SystemStubsExtension.class)
public class NetworkTest {

    private static final String testAccount = "111111111111";

    @SystemStub
    private EnvironmentVariables environmentVariables =
            new EnvironmentVariables(
                    WebConstants.JSII_SILENCE_WARNING_UNTESTED_NODE_VERSION, "true",
                    WebConstants.JSII_SILENCE_WARNING_DEPRECATED_NODE_VERSION, "true",
                    WebConstants.ACCOUNT_ENV, "test",
                    WebConstants.CDK_DEFAULT_ACCOUNT, testAccount,
                    WebConstants.CDK_DEFAULT_REGION, WebConstants.defaultRegion
            );


    @Test
    public void expectCertificateToExist() throws IOException {
        var stackProps = SimpleStackProps.Builder.create(NetworkStack.class).build();
        App app = new App();
        var stack = new NetworkStack(app, stackProps.getStackName(), stackProps);
        Template template = Template.fromStack(stack);

        template.resourceCountIs("AWS::CertificateManager::Certificate", 1);
        template.resourceCountIs("AWS::Route53::RecordSet", 2);
    }
}
