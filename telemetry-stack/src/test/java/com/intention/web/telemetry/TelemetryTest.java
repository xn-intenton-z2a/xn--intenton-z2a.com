// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web.telemetry;

import com.intention.web.constructs.SimpleStackProps;
import com.intention.web.utils.WebConstants;
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
public class TelemetryTest {

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
    public void expectBucketToHaveEnvironmentSpecificName() throws IOException {
        var stackProps = SimpleStackProps.Builder.create(TelemetryStack.class).build();
        App app = new App();
        var stack = new TelemetryStack(app, stackProps.getStackName(), stackProps);
        Template template = Template.fromStack(stack);

        template.resourceCountIs("AWS::S3::Bucket", 1);
        template.hasResourceProperties("AWS::S3::Bucket", new HashMap<>()
            {{ put("BucketName", "%s-web-intention-com-cloud-trail".formatted("test")); }});
    }
}
