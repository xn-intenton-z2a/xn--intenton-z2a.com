package com.intention.web.functions

--intenton-z2a.web.functions;

import com.xn--intenton-z2a.web.utils.WebConstants;
import com.xn--intenton-z2a.web.utils.S3;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(SystemStubsExtension.class)
public class LogGzippedS3ObjectTest {

    private static final String testAccount = "111111111111";
    private final String bucketName = "test-bucket";
    private final String objectKey = "test-key";

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
    public void expectHandlerToLogEvent() throws IOException {

        // Test parameters
        var objectContents = "test";

        // Expected results
        var expectedMessage = objectContents;

        // Mocks
        S3Event mockEvent = createMockS3Event(bucketName, objectKey, objectContents);
        Context mockContext = Mockito.mock(Context.class);
        LambdaLogger mockLogger = Mockito.mock(LambdaLogger.class);
        when(mockContext.getLogger()).thenReturn(mockLogger);
        S3 mockS3 = Mockito.mock(S3.class);
        when(mockS3.getZippedObjectContentAsString(mockEvent.getRecords().get(0).getS3())).thenReturn(objectContents);

        // Execute
        LogGzippedS3ObjectEvent handler = new LogGzippedS3ObjectEvent();
        handler.s3 = mockS3;
        handler.handleRequest(mockEvent, mockContext);

        // Verify
        Mockito.verify(mockLogger).log(expectedMessage);
    }

    @Test
    public void expectHandlerToLogException() throws IOException {

        // Test parameters
        var objectContents = "test";
        var exceptionMessage = "Test Exception";
        var exception = new RuntimeException(exceptionMessage);

        // Expected results
        var expectedMessage = exceptionMessage;

        // Mocks
        S3Event mockEvent = createMockS3Event(bucketName, objectKey, objectContents);
        Context mockContext = Mockito.mock(Context.class);
        LambdaLogger mockLogger = Mockito.mock(LambdaLogger.class);
        when(mockContext.getLogger()).thenReturn(mockLogger);
        S3 mockS3 = Mockito.mock(S3.class);
        when(mockS3.getZippedObjectContentAsString(mockEvent.getRecords().get(0).getS3())).thenThrow(exception);

        // Execute
        LogGzippedS3ObjectEvent handler = new LogGzippedS3ObjectEvent();
        handler.s3 = mockS3;
        try {
            handler.handleRequest(mockEvent, mockContext);
            fail("Exception should be thrown");
        } catch (RuntimeException e) {
            // Verify
            assertEquals(expectedMessage, e.getMessage());
        }
    }

    private S3Event createMockS3Event(String bucketName, String objectKey, String objectContents) {
        var schemaVersion = "1";
        var configurationId = "configurationId";
        var principle = "principle";

        S3Event mockEvent = Mockito.mock(S3Event.class);
        S3EventNotification.S3EventNotificationRecord mockRecord = Mockito.mock(S3EventNotification.S3EventNotificationRecord.class);
        S3EventNotification.UserIdentityEntity identity = new S3EventNotification.UserIdentityEntity(principle);
        S3EventNotification.S3BucketEntity bucket = new S3EventNotification.S3BucketEntity(
                bucketName,
                identity,
                WebConstants.defaultRegion);
        S3EventNotification.S3ObjectEntity mockObject = Mockito.mock(S3EventNotification.S3ObjectEntity.class);
        S3EventNotification.S3Entity s3Entity = new S3EventNotification.S3Entity(
                configurationId,
                bucket,
                mockObject,
                schemaVersion);

        when(mockEvent.getRecords()).thenReturn(Collections.singletonList(mockRecord));
        when(mockRecord.getS3()).thenReturn(s3Entity);
        when(mockObject.getKey()).thenReturn(objectKey);
        when(mockObject.toString()).thenReturn(objectContents);
        return mockEvent;
    }

}
