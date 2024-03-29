package com.quality.complaints.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "\\\\dash-files\\Quality\\Workspace\\Naeem\\Complaints";
  //  private String location = "c:/complaints/uploads";
  //  private String location = "src/main/upload-dir";

}