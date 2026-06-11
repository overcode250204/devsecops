package com.example.devsecopslab.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.lab")
public class LabProperties {

    private boolean vulnerableMode = true;

    private Set<String> allowedFetchHosts = new HashSet<>();
}
