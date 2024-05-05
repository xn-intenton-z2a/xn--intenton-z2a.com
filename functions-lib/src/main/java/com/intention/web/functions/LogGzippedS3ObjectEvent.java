package com.intention.web.functions;

import com.intention.web.utils.S3;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

import java.util.Arrays;

public class LogGzippedS3ObjectEvent implements RequestHandler<S3Event, Void> {

    public static final String handlerSource = "../functions-lib/target/functions-lib-0.1-SNAPSHOT.jar";

    public S3 s3 = new S3();

    @Override
    public Void handleRequest(S3Event s3event, Context context) {
        s3event.getRecords().forEach( record -> {
            var content = s3.getZippedObjectContentAsString(record.getS3());
            Arrays.stream(content.split("\n")).forEach(line -> {
                context.getLogger().log(line);
            });
        });
        return null;
    }

}
