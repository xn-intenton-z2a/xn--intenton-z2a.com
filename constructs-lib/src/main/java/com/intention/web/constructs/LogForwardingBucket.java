// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web.constructs;

import com.intention.web.functions.LogS3ObjectEvent;
import com.intention.web.utils.WebConstants;
import com.intention.web.utils.ResourceNameUtils;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.lambda.VersionProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.LifecycleRule;
import software.amazon.awscdk.services.s3.ObjectOwnership;
import software.amazon.awscdk.services.s3.notifications.LambdaDestination;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class LogForwardingBucket extends Stack {

    private static final Logger logger = LogManager.getLogger(LogForwardingBucket.class);

    public LogForwardingBucket(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public LogForwardingBucket(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);
        final String env = props != null && props.getTags() != null && props.getTags().get(WebConstants.envTag) != null
                ? props.getTags().get(WebConstants.envTag)
                : WebConstants.defaultEnv ;

        LogForwardingBucket.Builder
                .create(this, "OriginAccess", LogS3ObjectEvent.handlerSource, LogS3ObjectEvent.class)
                .build();
    }

    public static class Builder {

        private final Construct scope;
        private final String idPrefix;
        private final String handlerSource;
        private final Class<? extends RequestHandler<S3Event, ?>> handlerClass;
        private String bucketName = null;
        private String functionNamePrefix = null;

        private Builder(
                final Construct scope,
                final String idPrefix,
                final String handlerSource,
                final Class<? extends RequestHandler<S3Event, ?>> handlerClass) {
            this.scope = scope;
            this.idPrefix = idPrefix;
            this.handlerSource = handlerSource;
            this.handlerClass = handlerClass;
        }

        public static Builder create(
                final Construct scope,
                final String idPrefix,
                final String handlerSource,
                final Class<? extends RequestHandler<S3Event, ?>> handlerClass) {
            return new Builder(scope, idPrefix, handlerSource, handlerClass);
        }

        public Builder bucketName(String bucketName) {
            final Builder newBuilder = new Builder(scope, idPrefix, handlerSource, handlerClass);
            newBuilder.functionNamePrefix = functionNamePrefix;
            newBuilder.bucketName = bucketName;
            return newBuilder;
        }

        public Builder functionNamePrefix(String functionNamePrefix) {
            final Builder newBuilder = new Builder(scope, idPrefix, handlerSource, handlerClass);
            newBuilder.bucketName = bucketName;
            newBuilder.functionNamePrefix = functionNamePrefix;
            return newBuilder;
        }

        public IBucket build() {
            final String bucketName = buildBucketName();
            final String functionName = buildFunctionName();
            final IBucket logBucket = Bucket.Builder
                    .create(scope, "%sLogBucket".formatted(idPrefix))
                    .bucketName(bucketName)
                    .objectOwnership(ObjectOwnership.OBJECT_WRITER)
                    .versioned(false)
                    .encryption(BucketEncryption.S3_MANAGED)
                    .removalPolicy(RemovalPolicy.DESTROY)
                    .autoDeleteObjects(true)
                    .lifecycleRules(List.of(LifecycleRule.builder().expiration(Duration.days(30)).build()))
                    .build();
            logger.info("Created log bucket %s".formatted(logBucket.getBucketName()));
            final Function logForwarder = Function.Builder
                    .create(scope, "%sLogForwarder".formatted(idPrefix))
                    .functionName(functionName)
                    .runtime(Runtime.JAVA_11)
                    .code(Code.fromAsset(handlerSource))
                    .handler(handlerClass.getName())
                    .memorySize(1024)
                    .timeout(Duration.seconds(60))
                    .retryAttempts(2)
                    .build();
            CfnFunction cfnFunction = (CfnFunction) logForwarder.getNode().getDefaultChild();
            assert cfnFunction != null;
            cfnFunction.addPropertyOverride("SnapStart", Map.of("ApplyOn", "PublishedVersions"));
            new Version(scope, "%sLogForwarderVersion".formatted(idPrefix), VersionProps.builder()
                    .lambda(logForwarder)
                    .build());
            logger.info("Created log forwarder %s".formatted(logForwarder.getFunctionName()));
            logBucket.addEventNotification(EventType.OBJECT_CREATED, new LambdaDestination(logForwarder));
            logBucket.grantReadWrite(logForwarder);
            return logBucket;
        }

        private String buildFunctionName() {
            return functionNamePrefix != null
                    ? "%slog-forwarder".formatted(functionNamePrefix)
                    : "%s-log-forwarder".formatted(ResourceNameUtils.convertCamelCaseToDashSeparated(idPrefix));
        }

        private String buildBucketName() {
            return bucketName != null
                    ? bucketName
                    : "%s-log-bucket".formatted(ResourceNameUtils.convertCamelCaseToDashSeparated(idPrefix));
        }

    }
}
