package indi.vicliu.juaner.admin.client.vo;

import lombok.Data;

import java.util.List;

@Data
public class PomInfo {
    private String groupId;
    private String artifactId;
    private String version;
    private String location;
    private long size;
    private List<PomDependency> dependencies;
}
