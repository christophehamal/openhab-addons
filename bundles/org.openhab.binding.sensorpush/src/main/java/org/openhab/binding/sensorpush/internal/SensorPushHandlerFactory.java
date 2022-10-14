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
package org.openhab.binding.sensorpush.internal;

import static org.openhab.binding.sensorpush.internal.SensorPushBindingConstants.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

/**
 * The {@link SensorPushHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.sensorpush", service = ThingHandlerFactory.class)
public class SensorPushHandlerFactory extends BaseThingHandlerFactory {

    private static final Logger logger = LoggerFactory.getLogger(SensorPushHandlerFactory.class);
    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_ACCOUNT, THING_TYPE_SENSOR);
    private final HttpClient httpClient;
    private final Gson gson;

    @Activate
    public SensorPushHandlerFactory(@Reference HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.createHttpClient(BINDING_ID);
        // TODO: Add deserializers for temperature and humidity data types (and possibly others).
        this.gson = new GsonBuilder().registerTypeAdapter(ZonedDateTime.class,
                (JsonDeserializer<ZonedDateTime>) (json, type, JsonDeserializationContext) -> ZonedDateTime
                        .parse(json.getAsString(), DateTimeFormatter.RFC_1123_DATE_TIME))
                .create();
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_ACCOUNT.equals(thingTypeUID)) {
            return new SensorPushAccountHandler((Bridge) thing, httpClient, gson);
        } else if (THING_TYPE_SENSOR.equals(thingTypeUID)) {
            return new SensorPushSensorHandler(thing, gson);
        } else {
            logger.debug("Could not instantiate Handler for {}", thing.getLabel());
            return null;
        }
    }
}
