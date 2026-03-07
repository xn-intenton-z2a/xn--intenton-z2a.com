// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.ArrayList;
import java.util.List;

public class WebApp {

    public static void main(String[] args) {
        App app = new App();

        String envName = resolve(app, "ENVIRONMENT_NAME", "envName", "ci");
        String certificateArn = resolve(app, "CERTIFICATE_ARN", "certificateArn", "");

        String stackName = envName + "-web-stack";

        // Domain names depend on environment
        List<String> domainNames = new ArrayList<>();
        if ("prod".equals(envName)) {
            domainNames.add("xn--intenton-z2a.com");
        } else {
            domainNames.add(envName + ".web.xn--intenton-z2a.com");
        }

        // Record name for Route53 (apex for prod, subdomain for ci)
        String recordName = "prod".equals(envName) ? "" : envName + ".web";

        // Force us-east-1 — CloudFront requires certificate in us-east-1
        Environment awsEnv = Environment.builder()
                .region("us-east-1")
                .build();

        new WebStack(app, stackName, StackProps.builder()
                .env(awsEnv)
                .build(),
                envName, certificateArn, domainNames, recordName);

        app.synth();
    }

    /** Resolve a value from environment variable, then CDK context, then default. */
    private static String resolve(App app, String envVar, String contextKey, String defaultValue) {
        String value = System.getenv(envVar);
        if (value != null && !value.isBlank()) {
            return value;
        }
        Object contextValue = app.getNode().tryGetContext(contextKey);
        if (contextValue instanceof String s && !s.isBlank()) {
            return s;
        }
        return defaultValue;
    }
}
