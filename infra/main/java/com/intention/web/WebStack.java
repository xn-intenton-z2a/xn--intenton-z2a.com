// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web;

import software.amazon.awscdk.ArnComponents;
import software.amazon.awscdk.ArnFormat;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.services.certificatemanager.Certificate;
import software.amazon.awscdk.services.certificatemanager.ICertificate;
import software.amazon.awscdk.services.cloudfront.BehaviorOptions;
import software.amazon.awscdk.services.cloudfront.Distribution;
import software.amazon.awscdk.services.cloudfront.ErrorResponse;
import software.amazon.awscdk.services.cloudfront.HeadersFrameOption;
import software.amazon.awscdk.services.cloudfront.HeadersReferrerPolicy;
import software.amazon.awscdk.services.cloudfront.HttpVersion;
import software.amazon.awscdk.services.cloudfront.PriceClass;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersContentSecurityPolicy;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersContentTypeOptions;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersFrameOptions;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersPolicy;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersReferrerPolicy;
import software.amazon.awscdk.services.cloudfront.ResponseHeadersStrictTransportSecurity;
import software.amazon.awscdk.services.cloudfront.ResponseSecurityHeadersBehavior;
import software.amazon.awscdk.services.cloudfront.origins.S3BucketOrigin;
import software.amazon.awscdk.services.cloudfront.SSLMethod;
import software.amazon.awscdk.services.cloudfront.SecurityPolicyProtocol;
import software.amazon.awscdk.services.cloudfront.ViewerProtocolPolicy;
import software.amazon.awscdk.services.logs.CfnDelivery;
import software.amazon.awscdk.services.logs.CfnDeliveryDestination;
import software.amazon.awscdk.services.logs.CfnDeliverySource;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.route53.ARecord;
import software.amazon.awscdk.services.route53.AaaaRecord;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneAttributes;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.amazon.awscdk.services.route53.RecordTarget;
import software.amazon.awscdk.services.route53.targets.CloudFrontTarget;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.deployment.BucketDeployment;
import software.amazon.awscdk.services.s3.deployment.Source;
import software.constructs.Construct;

import java.util.List;

public class WebStack extends Stack {

    public WebStack(final Construct scope, final String id, final StackProps props,
                    final String envName, final String certificateArn,
                    final List<String> domainNames, final String recordName) {
        super(scope, id, props);

        // Tags
        Tags.of(this).add("Application", "intention-web");
        Tags.of(this).add("Environment", envName);
        Tags.of(this).add("Owner", "Polycode Limited");
        Tags.of(this).add("ManagedBy", "CDK");

        // S3 origin bucket
        Bucket originBucket = Bucket.Builder.create(this, "OriginBucket")
                .bucketName(envName + "-intention-com")
                .blockPublicAccess(BlockPublicAccess.BLOCK_ALL)
                .encryption(BucketEncryption.S3_MANAGED)
                .versioned(false)
                .removalPolicy(RemovalPolicy.RETAIN)
                .build();

        // Certificate (looked up by ARN — must be in us-east-1)
        ICertificate certificate = Certificate.fromCertificateArn(this, "Certificate", certificateArn);

        // Response headers policy with CSP for giscus
        ResponseHeadersPolicy headersPolicy = ResponseHeadersPolicy.Builder.create(this, "SecurityHeaders")
                .securityHeadersBehavior(ResponseSecurityHeadersBehavior.builder()
                        .contentSecurityPolicy(ResponseHeadersContentSecurityPolicy.builder()
                                .contentSecurityPolicy(
                                        "default-src 'self'; " +
                                        "script-src 'self' https://giscus.app; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "frame-src https://giscus.app; " +
                                        "img-src 'self' data:; " +
                                        "connect-src 'self'; " +
                                        "frame-ancestors 'none'; " +
                                        "form-action 'self' https://github.com")
                                .override(true)
                                .build())
                        .contentTypeOptions(ResponseHeadersContentTypeOptions.builder()
                                .override(true)
                                .build())
                        .frameOptions(ResponseHeadersFrameOptions.builder()
                                .frameOption(HeadersFrameOption.DENY)
                                .override(true)
                                .build())
                        .referrerPolicy(ResponseHeadersReferrerPolicy.builder()
                                .referrerPolicy(HeadersReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                                .override(true)
                                .build())
                        .strictTransportSecurity(ResponseHeadersStrictTransportSecurity.builder()
                                .accessControlMaxAge(Duration.seconds(63072000))
                                .includeSubdomains(true)
                                .preload(true)
                                .override(true)
                                .build())
                        .build())
                .build();

        // CloudFront distribution with OAC
        Distribution distribution = Distribution.Builder.create(this, "Distribution")
                .defaultBehavior(BehaviorOptions.builder()
                        .origin(S3BucketOrigin.withOriginAccessControl(originBucket))
                        .viewerProtocolPolicy(ViewerProtocolPolicy.REDIRECT_TO_HTTPS)
                        .responseHeadersPolicy(headersPolicy)
                        .build())
                .domainNames(domainNames)
                .certificate(certificate)
                .defaultRootObject("index.html")
                .httpVersion(HttpVersion.HTTP2_AND_3)
                .priceClass(PriceClass.PRICE_CLASS_100)
                .minimumProtocolVersion(SecurityPolicyProtocol.TLS_V1_2_2021)
                .sslSupportMethod(SSLMethod.SNI)
                .enableIpv6(true)
                .enableLogging(false)
                .errorResponses(List.of(
                        ErrorResponse.builder()
                                .httpStatus(404)
                                .responseHttpStatus(404)
                                .responsePagePath("/404-error-distribution.html")
                                .build()))
                .build();

        // CloudWatch Logs via CfnDelivery (access logs without S3/Lambda)
        LogGroup accessLogGroup = LogGroup.Builder.create(this, "AccessLogGroup")
                .logGroupName("/aws/cloudfront/" + envName + "-intention-com")
                .retention(RetentionDays.ONE_MONTH)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        // Construct CloudFront distribution ARN
        String distributionArn = formatArn(ArnComponents.builder()
                .service("cloudfront")
                .region("")
                .resource("distribution")
                .resourceName(distribution.getDistributionId())
                .arnFormat(ArnFormat.SLASH_RESOURCE_NAME)
                .build());

        CfnDeliveryDestination deliveryDestination = CfnDeliveryDestination.Builder.create(this, "DeliveryDestination")
                .name(envName + "-intention-com-logs-destination")
                .destinationResourceArn(accessLogGroup.getLogGroupArn())
                .build();

        CfnDeliverySource deliverySource = CfnDeliverySource.Builder.create(this, "DeliverySource")
                .name(envName + "-intention-com-logs-source")
                .logType("ACCESS_LOGS")
                .resourceArn(distributionArn)
                .build();

        CfnDelivery delivery = CfnDelivery.Builder.create(this, "Delivery")
                .deliveryDestinationArn(deliveryDestination.getAttrArn())
                .deliverySourceName(deliverySource.getName())
                .build();
        delivery.addDependency(deliveryDestination);
        delivery.addDependency(deliverySource);

        // Route53 records
        String hostedZoneId = "Z0315522208PWZSSBI9AL";
        String zoneName = "xn--intenton-z2a.com";

        IHostedZone hostedZone = HostedZone.fromHostedZoneAttributes(this, "HostedZone",
                HostedZoneAttributes.builder()
                        .hostedZoneId(hostedZoneId)
                        .zoneName(zoneName)
                        .build());

        RecordTarget target = RecordTarget.fromAlias(new CloudFrontTarget(distribution));

        // recordName is empty string for apex (prod), or "ci-web" etc. for branches
        ARecord.Builder.create(this, "AliasA")
                .zone(hostedZone)
                .recordName(recordName)
                .target(target)
                .build();

        AaaaRecord.Builder.create(this, "AliasAAAA")
                .zone(hostedZone)
                .recordName(recordName)
                .target(target)
                .build();

        // Deploy static content
        BucketDeployment.Builder.create(this, "DeployContent")
                .sources(List.of(Source.asset("public")))
                .destinationBucket(originBucket)
                .distribution(distribution)
                .distributionPaths(List.of("/index.html", "/404-error-distribution.html"))
                .retainOnDelete(true)
                .prune(false)
                .build();

        // Outputs
        CfnOutput.Builder.create(this, "DistributionDomainName")
                .value(distribution.getDistributionDomainName())
                .description("CloudFront distribution domain name")
                .build();

        CfnOutput.Builder.create(this, "BucketName")
                .value(originBucket.getBucketName())
                .description("S3 origin bucket name")
                .build();
    }
}
