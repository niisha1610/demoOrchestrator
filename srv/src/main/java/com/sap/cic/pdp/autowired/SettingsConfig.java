/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.http.HttpClient;
import java.time.Clock;

@Configuration
public class SettingsConfig {
    @Bean
    HttpClient getHttpClient() {
        return HttpClient.newBuilder().build();
    }
}
