package indi.vicliu.juaner.admin.client.utils;

import com.google.common.collect.Lists;
import com.sun.tools.classfile.Dependency;
import indi.vicliu.juaner.admin.client.vo.JarDependencies;
import indi.vicliu.juaner.admin.client.vo.PomDependency;
import indi.vicliu.juaner.admin.client.vo.PomInfo;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.io.ClassPathResource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class Analyzer {
    public static JarDependencies getAllPomInfo() throws Exception {
        JarDependencies jarDependencies = new JarDependencies();
        List<PomInfo> pomInfos = getAllJarPomInfo();
        jarDependencies.setPomInfos(pomInfos);
        jarDependencies.setSpringBootVersion(SpringBootVersion.getVersion());
        Optional<PomInfo> optionalPomInfo = pomInfos.stream().filter( //
                x -> (StringUtils.isNotEmpty(x.groupId) && x.groupId.equals("org.springframework.cloud")
                        &&x.artifactId.equals("spring-cloud-commons")))
                .findFirst();
        setSpringCloudDeps(optionalPomInfo, jarDependencies);

        return jarDependencies;
    }

    private static List<PomInfo> getAllJarPomInfo() throws IOException {
        List<PomInfo> pomInfos = Lists.newArrayList();
        String metaPath = "META-INF";

        Enumeration<URL> urls = Analyzer.class.getClassLoader().getResources(metaPath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null && "jar".equals(url.getProtocol())) {
                String urlStr = url.toString();
                log.debug("url-str: " + urlStr);
                String location = urlStr.substring(urlStr.indexOf('f'), urlStr.lastIndexOf('!'));
                log.debug("location: " + location);
                readPomInfo(location, pomInfos);
            }
        }

        return pomInfos;
    }

    private static void readPomInfo(String location, List<PomInfo> pomInfos) {
        Properties properties = new Properties();
        String metaPath = "META-INF";
        try {
            InputStream is;
            if (location.contains("!")) {
                // read inside jar's
                is = new ClassPathResource(location.substring(location.indexOf("!") + 1)).getInputStream();
            } else {
                URL realUrl = new URL(location);
                is = realUrl.openStream();
            }

            PomInfo pomInfo = null;
            ZipInputStream zip = new ZipInputStream(is);
            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null) {
                String zipEntryPath = zipEntry.getName();
                if (zipEntryPath.startsWith(metaPath + "/maven") && zipEntryPath.endsWith("pom.xml")) {
                    log.debug("zipEntryPath: {}",zipEntryPath);
                    pomInfo = readPom(zip);
                    break;
                }

                if (zipEntryPath.equals(metaPath + "/MANIFEST.MF")) {
                    properties.load(zip);
                }
            }

            // calculate jar size
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            IOUtil.copy(is, output);
            long jarSize = output.toByteArray().length;

            if (null != pomInfo) {
                pomInfo.setLocation(location);
                pomInfo.setSize(jarSize);
                pomInfos.add(pomInfo);
            }
        } catch (Exception e) {
            log.error("get jar maven pom failed! location:" + location, e);
        }
    }

    private static PomInfo readPom(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        if (null != is) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            try {
                Model model = reader.read(is);
                PomInfo pomInfo = new PomInfo();
                pomInfo.setArtifactId(model.getArtifactId());
                if (StringUtils.isEmpty(model.getGroupId())) {
                    pomInfo.setGroupId(model.getParent().getGroupId());
                } else {
                    pomInfo.setGroupId(model.getGroupId());
                }
                if (StringUtils.isEmpty(model.getVersion())) {
                    pomInfo.setVersion(model.getParent().getVersion());
                } else {
                    pomInfo.setVersion(model.getVersion());
                }
                List<Dependency> dependencies = model.getDependencies();
                List<PomDependency> pomDependencies = Lists.newArrayList();
                for (Dependency dependency : dependencies) {
                    PomDependency pomDependency = new PomDependency();
                    String groupId = dependency.getGroupId();
                    if (StringUtils.isNotEmpty(groupId) && (groupId.equals("${project.groupId}"))) {
                        groupId = pomInfo.groupId;
                    }
                    pomDependency.setGroupId(groupId);
                    pomDependency.setArtifactId(dependency.getArtifactId());
                    String version = dependency.getVersion();
                    if (StringUtils.isNotEmpty(version) && (version.startsWith("${") && version.endsWith("}"))) {
                        version = model.getProperties().getProperty(version.substring(2, version.length() - 1));
                    }
                    pomDependency.setVersion(version);

                    pomDependency.setScope(dependency.getScope());
                    pomDependencies.add(pomDependency);
                }
                pomInfo.setDependencies(pomDependencies);
                return pomInfo;
            } catch (XmlPullParserException e) {
                log.error("read pom failed!" + e.getMessage(),e);
            }
        }
        return null;
    }

    private static void setSpringCloudDeps(Optional<PomInfo> optionalPomInfo, //
                                           JarDependencies jarDependencies) throws Exception {
        if (optionalPomInfo.isPresent()) {
            PomInfo pomInfo = optionalPomInfo.get();
            String sccommonsVersion = pomInfo.version;
            if (StringUtils.isNotEmpty(sccommonsVersion)) {
                String scVersion= SpringCloudVersionEnum.getScVersionByCommonVersion(sccommonsVersion);
                jarDependencies.setSpringCloudVersion(scVersion);
            }
        }
    }
}
