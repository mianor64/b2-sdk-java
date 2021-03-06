/*
 * Copyright 2017, Backblaze Inc. All Rights Reserved.
 * License https://www.backblaze.com/using_b2_code.html
 */
package com.backblaze.b2.client.structures;

import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.exceptions.B2LocalException;
import com.backblaze.b2.json.B2Json;
import com.backblaze.b2.util.B2Collections;
import com.backblaze.b2.util.B2Preconditions;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.backblaze.b2.client.contentSources.B2Headers.LARGE_FILE_SHA1_INFO_NAME;

public class B2StartLargeFileRequest {
    @B2Json.required
    private final String bucketId;
    @B2Json.required
    private final String fileName;
    @B2Json.required
    private final String contentType;
    @B2Json.optional
    private final Map<String, String> fileInfo;

    @B2Json.constructor(params = "bucketId,fileName,contentType,fileInfo")
    private B2StartLargeFileRequest(String bucketId,
                                    String fileName,
                                    String contentType,
                                    Map<String, String> fileInfo) {
        this.bucketId = bucketId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileInfo = B2Collections.unmodifiableMap(fileInfo);
    }

    public String getBucketId() {
        return bucketId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> getFileInfo() {
        return fileInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        B2StartLargeFileRequest that = (B2StartLargeFileRequest) o;
        return Objects.equals(getBucketId(), that.getBucketId()) &&
                Objects.equals(getFileName(), that.getFileName()) &&
                Objects.equals(getContentType(), that.getContentType()) &&
                Objects.equals(getFileInfo(), that.getFileInfo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBucketId(), getFileName(), getContentType(), getFileInfo());
    }

    public static B2StartLargeFileRequest buildFrom(B2UploadFileRequest orig) throws B2Exception {
        try {
            final Map<String,String> fileInfo;

            final String largeFileSha1 = orig.getContentSource().getSha1OrNull();
            if (largeFileSha1 == null) {
                // no largeFileSha1, so just use the original fileInfo
                fileInfo = orig.getFileInfo();
            } else {
                // there's a largeFileSha1, so use it.

                // if there already was one in the request, make sure it matches.
                final String origLargeFileSha1 = orig.getFileInfo().get(LARGE_FILE_SHA1_INFO_NAME);
                B2Preconditions.checkArgument(
                        origLargeFileSha1 == null ||
                                Objects.equals(largeFileSha1, origLargeFileSha1),
                        "already have largeFileSha1 and it doesn't match?");

                fileInfo = new TreeMap<>();
                fileInfo.putAll(orig.getFileInfo());
                fileInfo.put(LARGE_FILE_SHA1_INFO_NAME, largeFileSha1);
            }

            return new B2StartLargeFileRequest(
                    orig.getBucketId(),
                    orig.getFileName(),
                    orig.getContentType(),
                    fileInfo
            );
        } catch (IOException e) {
            throw new B2LocalException("local", "failed to get large file's sha1 from contentSource: " + e.getMessage(), e);
        }
    }


//    public static Builder builder(String bucketId,
//                                  String fileName,
//                                  String contentType) {
//        return new Builder(bucketId, fileName, contentType);
//    }
//
//    public static class Builder {
//        private String bucketId;
//        private String fileName;
//        private String contentType;
//        private Map<String, String> fileInfo;
//
//        Builder(String bucketId,
//                String fileName,
//                String contentType) {
//            this.bucketId = bucketId;
//            this.fileName = fileName;
//            this.contentType = contentType;
//            this.fileInfo = new TreeMap<>();
//        }
//
//        /**
//         * Sets one of your custom fields to be this well-known field.
//         * @param lastModifiedMillis the time the "source" of this file was last modified.
//         */
//        public Builder setSrcLastModifiedMillisOrNull(long lastModifiedMillis) {
//            setCustomField("src_last_modified_millis", Long.toString(lastModifiedMillis));
//            return this;
//        }
//
//        public Builder setCustomField(String name, String value) {
//            fileInfo.put(name, value);
//            return this;
//        }
//
//        public B2StartLargeFileRequest build() {
//            return new B2StartLargeFileRequest(bucketId,
//                    fileName,
//                    contentType,
//                    fileInfo);
//        }
//    }
}
