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
package org.openhab.binding.sensorpush.internal.dto;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link SampleList} provides the DTO for samples retrieved from the SensorPush API.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
public class SampleList {

    private ZonedDateTime lastTime = ZonedDateTime.parse("2010-01-01T10:00:00+01:00[Europe/Paris]");
    private Map<String, List<Sample>> sensors = new HashMap<>();
    private String status = "";
    private double totalSamples;
    private double totalSensors;
    private boolean truncated;

    public ZonedDateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(ZonedDateTime lastTime) {
        this.lastTime = lastTime;
    }

    public Map<String, List<Sample>> getSensors() {
        return sensors;
    }

    public void setSensors(Map<String, List<Sample>> sensors) {
        this.sensors = sensors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalSamples() {
        return totalSamples;
    }

    public void setTotalSamples(double totalSamples) {
        this.totalSamples = totalSamples;
    }

    public double getTotalSensors() {
        return totalSensors;
    }

    public void setTotalSensors(double totalSensors) {
        this.totalSensors = totalSensors;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }
}
