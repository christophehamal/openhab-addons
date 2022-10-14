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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link Sample} provides the DTO for samples retrieved from the SensorPush API.
 *
 * @author Christophe Hamal - Initial contribution
 */
@NonNullByDefault
public class Sample {
    private double altitude; // in ft
    private double barometricPressure; // in Hg
    private double dewpoint; // in *F
    private double humidity; // in %
    private String observed = "";

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private List<String> tags = new ArrayList<>();
    private double temperature; // in *F
    private double vpd; // in kPa

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getBarometricPressure() {
        return barometricPressure;
    }

    public void setBarometricPressure(double barometricPressure) {
        this.barometricPressure = barometricPressure;
    }

    public double getDewpoint() {
        return dewpoint;
    }

    public void setDewpoint(double dewpoint) {
        this.dewpoint = dewpoint;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getObserved() {
        return observed;
    }

    public void setObserved(String observed) {
        this.observed = observed;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getVpd() {
        return vpd;
    }

    public void setVpd(double vpd) {
        this.vpd = vpd;
    }
}
