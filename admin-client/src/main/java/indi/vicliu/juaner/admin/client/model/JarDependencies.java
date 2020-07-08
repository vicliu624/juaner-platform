package indi.vicliu.juaner.admin.client.model;

import lombok.Data;

import java.util.List;

@Data
public class JarDependencies {
    private String summerframeworkVersion;
    private String springCloudVersion;
    private String springBootVersion;
    private List<PomInfo> pomInfos;
}
