/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.sensorpush.internal.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.openhab.binding.sensorpush.internal.SensorPushConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * The {@link SensorPushAPI} wraps the SensorPush API.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
public class SensorPushAPI {

    private final Logger logger = LoggerFactory.getLogger(SensorPushAPI.class);

    // API Fields
    private static final String JSON_CONTENT_TYPE = "application/json";
    private final String BASE_URL = "https://api.sensorpush.com/api/v1";
    private final String ACCESSTOKEN_URL = BASE_URL + "/oauth/accesstoken";
    private final String AUTHORIZE_URL = BASE_URL + "/oauth/authorize";
    private final String SAMPLES_URL = BASE_URL + "/samples";
    private final String SENSORS_URL = BASE_URL + "/devices/sensors";
    private final long AUTHORIZATION_TOKEN_TIMEOUT = 60;
    private final long ACCESS_TOKEN_TIMEOUT = 30;
    private @Nullable ZonedDateTime authorizationTokenTimeStamp;
    private @Nullable ZonedDateTime accessTokenTimeStamp;
    private @Nullable String authorizationToken = "";
    private @Nullable String accessToken = "";

    // TODO: change this to more secure.
    private final String email = "christophehamal@hotmail.com";
    private final String password = "P@trese78";

    private final SensorPushConfiguration config;
    private final HttpClient httpClient;
    private final Gson gson;

    public SensorPushAPI(SensorPushConfiguration config, HttpClient httpClient, Gson gson) {
        this.config = config;
        this.httpClient = httpClient;
        this.gson = gson;
        httpClient.setDefaultRequestContentType(JSON_CONTENT_TYPE);
        httpClient.setIdleTimeout(5000);
        try {
            httpClient.start();
        } catch (Exception e) {
            logger.debug("Could not start HttpClient: {}", e.getMessage());
        }
    }

    /**
     * Helper method to send an HTTP POST request to the SensorPush API
     *
     * @param bodyMap Key-value pairs of the JSON to be included as a body in the API call
     * @return response if successful, null if not
     */
    @Nullable
    private ContentResponse PostRequest(Map<String, String> bodyMap) {
        try {
            return httpClient.newRequest(ACCESSTOKEN_URL).header(HttpHeader.ACCEPT, JSON_CONTENT_TYPE)
                    .header(HttpHeader.CACHE_CONTROL, "no-cache").method(HttpMethod.POST)
                    .content(new StringContentProvider(JSON_CONTENT_TYPE, gson.toJson(bodyMap), StandardCharsets.UTF_8))
                    .send();
        } catch (TimeoutException e) {
            logger.debug("Refresh call timed out: {}", e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            logger.debug("Refresh call could not complete: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Helper method to refresh the access token while the authorization token is still valid
     *
     * @return access token if successful, null if not
     */
    @Nullable
    private String refreshAccessToken() {
        logger.debug("Obtaining a new access token, using the authorization token");
        Map<String, String> bodyMap = new HashMap<>();
        String token = authorizationToken;
        if (token != null) {
            bodyMap.put("authorization", token);
        }
        ContentResponse response = PostRequest(bodyMap);
        if (response != null) {
            try {
                TypeToken<Map<String, String>> responseMapType = new TypeToken<Map<String, String>>() {
                };
                Map<String, String> responseMap;
                String localAccessToken = "";
                responseMap = gson.fromJson(response.getContentAsString(), responseMapType.getType());
                if (HttpStatus.isSuccess(response.getStatus()) && !responseMap.isEmpty()) {
                    localAccessToken = (responseMap.get("accesstoken") != null ? responseMap.get("accesstoken") : "");
                    accessTokenTimeStamp = ZonedDateTime.parse(response.getHeaders().get(HttpHeader.DATE));
                    accessToken = localAccessToken;
                    return accessToken;
                } else
                    throw new IOException(String.valueOf(response.getStatus()) + " - " + response.getReason());
            } catch (Exception e) {
                logger.debug("Refresh call could not complete: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Helper method to obtain an authorization token with email / password combination
     *
     * @return authorization token if successful, null if not
     */
    @Nullable
    private String authorize() {
        logger.debug("Obtaining a new authorization token, using email / password");
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("email", email);
        bodyMap.put("password", password);
        ContentResponse response = PostRequest(bodyMap);
        if (response != null) {
            try {
                TypeToken<Map<String, String>> responseMapType = new TypeToken<Map<String, String>>() {
                };
                Map<String, String> responseMap = new HashMap<>();
                if (HttpStatus.isSuccess(response.getStatus())) {
                    responseMap = gson.fromJson(response.getContentAsString(), responseMapType.getType());
                    if (responseMap.get("authorization") != null) {
                        authorizationToken = responseMap.get("authorization");
                    }
                    authorizationTokenTimeStamp = ZonedDateTime.parse(response.getHeaders().get(HttpHeader.DATE));
                    return authorizationToken;
                } else
                    throw new IOException(String.valueOf(response.getStatus()) + " - " + response.getReason());
            } catch (Exception e) {
                logger.debug("Authorization call could not complete: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Main method to handle the oauth flow of the SensorPush API
     *
     * @return access token if successful, null if not
     */
    @Nullable
    public String getAccessToken() {
        if (!"".equals(accessToken)
                && ZonedDateTime.now().isBefore(accessTokenTimeStamp.plus(Duration.ofMinutes(ACCESS_TOKEN_TIMEOUT)))) {
            return accessToken;
        } else if (!"".equals(authorizationToken) && ZonedDateTime.now()
                .isBefore(authorizationTokenTimeStamp.plus(Duration.ofMinutes(AUTHORIZATION_TOKEN_TIMEOUT)))) {
            refreshAccessToken();
        } else {
            authorize();
            refreshAccessToken();
        }
        if ("".equals(accessToken)) {
            logger.debug("Could not obtain a valid access token");
            return "";
        }
        return accessToken;
    }

    public void dispose() {
        try {
            httpClient.stop();
        } catch (Exception e) {
            logger.debug("Could not stop HttpClient: {}", e.getMessage());
        }
    }
}
