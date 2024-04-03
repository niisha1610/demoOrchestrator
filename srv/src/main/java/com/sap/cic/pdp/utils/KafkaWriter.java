package com.sap.cic.pdp.utils;

import com.azure.storage.file.datalake.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cic.pdp.entity.Configuration;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

import static com.sap.cic.pdp.constants.SchedularConstants.CONFIG_YAML_FILE;

@Component
public class KafkaWriter {
    public String writeKafkaJson(String jsonString) {

        String directoryPath = "kafka";
        String fileName = "adjustKafka.json";
        Configuration config = ConfigLoadUtils.getConfiguration(CONFIG_YAML_FILE);
        String filePath = directoryPath + "/" + fileName;

        // Use SAS token for authentication
        String connectionString = "https://" + config.getAccountName() + ".blob.core.windows.net/" + config.getContainerName() + "?" + config.getSasToken();
        DataLakeFileSystemClient blobServiceClient = new DataLakeFileSystemClientBuilder()
                .endpoint(connectionString)
                .fileSystemName(config.getContainerName())
                .buildClient();
        try {
            // Create the directory if it doesn't exist
            blobServiceClient.createDirectoryIfNotExists(directoryPath);
            // Convert JSON string to a byte array
            ObjectMapper mapper = new ObjectMapper();
            // Replace with your preferred
            byte[] jsonDataBytes = mapper.writeValueAsBytes(jsonString);
            DataLakeFileClient dataLakeFileClient = blobServiceClient.getFileClient(filePath);
            // Create the file if it doesn't exist and upload the data
            dataLakeFileClient.createIfNotExists();
            dataLakeFileClient.upload(new ByteArrayInputStream(jsonDataBytes), jsonDataBytes.length, true);

            System.out.println("JSON file uploaded successfully to: " + dataLakeFileClient.getFilePath());
        } catch (Exception e) {
            System.out.println("Error uploading file: " + e.getMessage());
        }
        return filePath;
    }
}