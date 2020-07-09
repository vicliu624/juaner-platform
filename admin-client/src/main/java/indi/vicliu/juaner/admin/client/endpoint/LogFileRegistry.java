package indi.vicliu.juaner.admin.client.endpoint;

import indi.vicliu.juaner.admin.client.model.LogFile;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties("logging.registry")
public class LogFileRegistry {

    private List<LogFile> files;

}
