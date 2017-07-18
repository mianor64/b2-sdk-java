/*
 * Copyright 2017, Backblaze Inc. All Rights Reserved.
 * License https://www.backblaze.com/using_b2_code.html
 */
package com.backblaze.b2.client.webApiHttpClient;

import com.backblaze.b2.client.B2AccountAuthorizer;
import com.backblaze.b2.client.B2AccountAuthorizerSimpleImpl;
import com.backblaze.b2.client.B2ClientConfig;
import com.backblaze.b2.client.B2Sdk;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientImpl;
import com.backblaze.b2.client.B2StorageClientWebifier;
import com.backblaze.b2.client.B2StorageClientWebifierImpl;
import com.backblaze.b2.client.credentialsSources.B2Credentials;
import com.backblaze.b2.client.credentialsSources.B2CredentialsFromEnvironmentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.webApiClients.B2WebApiClient;
import com.backblaze.b2.util.B2Preconditions;

public class B2StorageHttpClientBuilder {

    private static final String DEFAULT_MASTER_URL = "https://api.backblazeb2.com/";
    private final B2ClientConfig config;
    private B2WebApiClient webApiClient;

    public static B2StorageHttpClientBuilder builder(B2ClientConfig config) {
        return new B2StorageHttpClientBuilder(config);
    }

    // We don't usually have several builder() methods, but this builder
    // is used by *everyone*, so i want to make it so that most users don't
    // need to worry about making an authorizer.
    public static B2StorageHttpClientBuilder builder(String accountId, String applicationKey, String userAgent) {
        final B2AccountAuthorizer accountAuthorizer = B2AccountAuthorizerSimpleImpl
                .builder(accountId, applicationKey)
                .build();
        final B2ClientConfig config = B2ClientConfig
                .builder(accountAuthorizer, userAgent)
                .build();
        return builder(config);
    }

    /**
     * @param userAgent the user agent to use when performing http requests.
     * @return a storage builder.
     * @throws B2Exception if there's a problem getting the credentials from the environment.
     */
    public static B2StorageHttpClientBuilder builder(String userAgent) throws B2Exception {
        final B2Credentials credentials = B2CredentialsFromEnvironmentSource.build().getCredentials();
        return builder(credentials.getAccountId(), credentials.getApplicationKey(), userAgent);
    }

    private B2StorageHttpClientBuilder(B2ClientConfig config) {
        this.config = config;
    }

    public B2StorageHttpClientBuilder setWebApiClient(B2WebApiClient webApiClient) {
        B2Preconditions.checkState(this.webApiClient == null, "already set?");
        this.webApiClient = webApiClient;
        return this;
    }

    // setTimeouts
    // setRetryPolicy (useUploadNonce?)

    public B2StorageClient build() {
        final B2WebApiClient webApiClient = (this.webApiClient != null) ?
                this.webApiClient :
                new B2WebApiClientImpl();
        final B2StorageClientWebifier webifier = new B2StorageClientWebifierImpl(
                webApiClient,
                config.getUserAgent() + " " + B2Sdk.getName() + "/" + B2Sdk.getVersion(),
                (config.getMasterUrl() == null) ? DEFAULT_MASTER_URL : config.getMasterUrl(),
                config.getTestModeOrNull());
        return new B2StorageClientImpl(
                webifier,
                config);
    }
}
