package com.intention.web.telemetry;

import com.intention.web.utils.ResourceNameUtils;
import com.intention.web.utils.WebConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.cloudtrail.Trail;
import software.amazon.awscdk.services.cloudtrail.TrailProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketEncryption;
import software.amazon.awscdk.services.s3.IBucket;
import software.amazon.awscdk.services.s3.LifecycleRule;
import software.amazon.awscdk.services.s3.ObjectOwnership;
import software.constructs.Construct;

import java.util.List;

public class TelemetryStack extends Stack {

    private static final Logger logger = LogManager.getLogger(TelemetryStack.class);

    private String domainName(String env) { return "%s.web.%s".formatted(env, WebConstants.hostedZoneName); }
    private String dashedDomainName(String env) { return ResourceNameUtils.convertDashSeparatedToDotSeparated(domainName(env), WebConstants.domainNameMappings); }
    private String cloudTrailLogBucketName(String env) { return "%s-cloud-trail".formatted(dashedDomainName(env)); }

    public TelemetryStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public TelemetryStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final String env = props != null && props.getTags() != null && props.getTags().get(WebConstants.envTag) != null
                ? props.getTags().get(WebConstants.envTag)
                : WebConstants.defaultEnv ;

        // Enable CloudTrail logging for S3 and Lambdas
        final IBucket cloudTrailLogBucket = Bucket.Builder
                .create(this, "CloudTrailBucket")
                .bucketName(cloudTrailLogBucketName(env))
                .objectOwnership(ObjectOwnership.OBJECT_WRITER)
                .versioned(false)
                .encryption(BucketEncryption.S3_MANAGED)
                .lifecycleRules(List.of(LifecycleRule.builder().expiration(Duration.days(30)).build()))
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();
        final Trail trail = new Trail(this, "CloudTrail", TrailProps.builder()
                .bucket(cloudTrailLogBucket)
                .trailName(id)
                .isMultiRegionTrail(true)
                .includeGlobalServiceEvents(true)
                .sendToCloudWatchLogs(true)
                .build());
        trail.logAllS3DataEvents();
        trail.logAllLambdaDataEvents();
    }
}
