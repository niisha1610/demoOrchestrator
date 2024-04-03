package com.sap.cic.pdp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>This class gets configuration from config yaml.</p>
 */
@Data
@NoArgsConstructor
public class Configuration implements Serializable {
    private String masterDataDeltaLoadURL;
    private String accountName;
    private String containerName;
    private String sasToken;
}
