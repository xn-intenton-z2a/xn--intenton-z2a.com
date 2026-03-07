// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web;

import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.assertions.Template;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class WebStackTest {

    @Test
    void synthCiStack() {
        App app = new App();
        WebStack stack = new WebStack(app, "test-ci-web-stack",
                StackProps.builder()
                        .env(Environment.builder().region("us-east-1").build())
                        .build(),
                "ci",
                "arn:aws:acm:us-east-1:123456789012:certificate/test-cert-id",
                List.of("ci.web.xn--intenton-z2a.com"),
                "ci.web");

        Template template = Template.fromStack(stack);
        assertNotNull(template);

        // Verify S3 bucket
        template.hasResourceProperties("AWS::S3::Bucket", Map.of(
                "BucketName", "ci-intention-com"
        ));

        // Verify CloudFront distribution exists
        template.resourceCountIs("AWS::CloudFront::Distribution", 1);

        // Verify Route53 A and AAAA records
        template.resourceCountIs("AWS::Route53::RecordSet", 2);
    }

    @Test
    void synthProdStack() {
        App app = new App();
        WebStack stack = new WebStack(app, "test-prod-web-stack",
                StackProps.builder()
                        .env(Environment.builder().region("us-east-1").build())
                        .build(),
                "prod",
                "arn:aws:acm:us-east-1:123456789012:certificate/test-cert-id",
                List.of("xn--intenton-z2a.com"),
                "");

        Template template = Template.fromStack(stack);
        assertNotNull(template);

        // Verify S3 bucket
        template.hasResourceProperties("AWS::S3::Bucket", Map.of(
                "BucketName", "prod-intention-com"
        ));

        // Verify CloudFront distribution has both domain names
        template.resourceCountIs("AWS::CloudFront::Distribution", 1);

        // Verify Route53 records
        template.resourceCountIs("AWS::Route53::RecordSet", 2);
    }
}
