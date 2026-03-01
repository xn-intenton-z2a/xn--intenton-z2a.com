// SPDX-License-Identifier: AGPL-3.0-only
// Copyright (C) 2025-2026 Polycode Limited
package com.intention.web.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class MockableBuilder {

    public GetObjectRequest getObjectRequest(String s3BucketName, String s3ObjectKey) {
        return GetObjectRequest
                .builder()
                .bucket(s3BucketName)
                .key(s3ObjectKey)
                .build();
    }

    public ResponseTransformer<GetObjectResponse, ResponseInputStream<GetObjectResponse>> getResponseTransformer() {
        return ResponseTransformer.toInputStream();
    }
}
