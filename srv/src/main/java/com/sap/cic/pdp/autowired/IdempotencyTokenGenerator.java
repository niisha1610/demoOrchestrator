/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.autowired;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdempotencyTokenGenerator {
    public String generateIdempotencyToken() {
        return UUID.randomUUID().toString();
    }
}
