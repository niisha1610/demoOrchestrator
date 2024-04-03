/*
 * ************************************************************************
 *  (C) 2024 SAP SE or an SAP affiliate company. All rights reserved. *
 * ************************************************************************
 */

package com.sap.cic.pdp.jsonStructures;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteJobRequestJsonStr {
    @JsonProperty("job_id")
    private Long jobId;
}
