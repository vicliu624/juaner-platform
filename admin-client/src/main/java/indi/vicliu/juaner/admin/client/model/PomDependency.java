package indi.vicliu.juaner.admin.client.model;

import lombok.Data;

@Data
public class PomDependency {
    private String groupId;
    private String artifactId;
    private String version;
    private String scope;
}
