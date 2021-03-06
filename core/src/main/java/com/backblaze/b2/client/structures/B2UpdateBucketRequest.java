/*
 * Copyright 2017, Backblaze Inc. All Rights Reserved.
 * License https://www.backblaze.com/using_b2_code.html
 */
package com.backblaze.b2.client.structures;

import com.backblaze.b2.json.B2Json;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class B2UpdateBucketRequest {
    @B2Json.required
    private final String accountId;

    @B2Json.required
    private final String bucketId;

    @B2Json.optional
    private final String bucketType;

    @B2Json.optional
    private final Map<String, String> bucketInfo;

    @B2Json.optional
    private final List<B2LifecycleRule> lifecycleRules;

    @B2Json.optional
    private final Integer ifRevisionIs;

    @B2Json.constructor(params = "accountId,bucketId,bucketType,bucketInfo,lifecycleRules,ifRevisionIs")
    private B2UpdateBucketRequest(String accountId,
                                  String bucketId,
                                  String bucketType,
                                  Map<String, String> bucketInfo,
                                  List<B2LifecycleRule> lifecycleRules,
                                  Integer ifRevisionIs) {
        this.accountId = accountId;
        this.bucketId = bucketId;
        this.bucketType = bucketType;
        this.bucketInfo = bucketInfo;
        this.lifecycleRules = lifecycleRules;
        this.ifRevisionIs = ifRevisionIs;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBucketId() {
        return bucketId;
    }

    public String getBucketType() {
        return bucketType;
    }

    public Map<String, String> getBucketInfo() {
        return bucketInfo;
    }

    public List<B2LifecycleRule> getLifecycleRules() {
        return lifecycleRules;
    }

    public Integer getIfRevisionIs() {
        return ifRevisionIs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        B2UpdateBucketRequest that = (B2UpdateBucketRequest) o;
        return Objects.equals(getAccountId(), that.getAccountId()) &&
                Objects.equals(getBucketId(), that.getBucketId()) &&
                Objects.equals(getBucketType(), that.getBucketType()) &&
                Objects.equals(getBucketInfo(), that.getBucketInfo()) &&
                Objects.equals(getLifecycleRules(), that.getLifecycleRules()) &&
                Objects.equals(getIfRevisionIs(), that.getIfRevisionIs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountId(), getBucketId(), getBucketType(), getBucketInfo(), getLifecycleRules(), getIfRevisionIs());
    }

    public static Builder builder(B2Bucket bucket) {
        return new Builder(bucket);
    }

    public static class Builder {
        // these are taken from the bucket and not change.
        // XXX: should i let people opt-out of using ifRevisionIs? let's see if anyone needs it!
        private final String accountId;
        private final String bucketId;
        private final Integer ifRevisionIs;

        // these are null unless they're set, since null means don't change.
        private String bucketType;
        private Map<String, String> bucketInfo;
        private List<B2LifecycleRule> lifecycleRules;


        private Builder(B2Bucket bucket) {
            this.accountId = bucket.getAccountId();
            this.bucketId = bucket.getBucketId();
            this.ifRevisionIs = bucket.getRevision();
        }

        public Builder setBucketType(String bucketType) {
            this.bucketType = bucketType;
            return this;
        }

        public Builder setBucketInfo(Map<String, String> bucketInfo) {
            this.bucketInfo = bucketInfo;
            return this;
        }

        public Builder setLifecycleRules(List<B2LifecycleRule> lifecycleRules) {
            this.lifecycleRules = lifecycleRules;
            return this;
        }


        public B2UpdateBucketRequest build() {
            return new B2UpdateBucketRequest(
                    accountId,
                    bucketId,
                    bucketType,
                    bucketInfo,
                    lifecycleRules,
                    ifRevisionIs);
        }
    }
}
