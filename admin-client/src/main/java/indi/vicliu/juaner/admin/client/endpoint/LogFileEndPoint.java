package indi.vicliu.juaner.admin.client.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@WebEndpoint(id = "logfile")
public class LogFileEndPoint {
    private LogFileRegistry registry;

    public LogFileEndPoint(Environment environment, LogFileRegistry registry) {
        this.registry = registry;
    }

    @ReadOperation
    @ResponseBody
    public ListNamesResponse listNames() throws IOException {
        return new ListNamesResponse(collectNames(this.registry));
    }

    private List<String> collectNames(LogFileRegistry registry) throws IOException {
        List<File> logFiles = new ArrayList<>();
        registry.getFiles().forEach( path -> {
            File file = new File(path.getPath());
            if(file.exists() && file.isDirectory()){
                File[] subFiles = file.listFiles();
                if(subFiles != null){
                    List<File> allFile = Arrays.asList(subFiles);
                    allFile.forEach( x -> {
                        Pattern r = Pattern.compile(path.getFilename());
                        Matcher m = r.matcher(x.getName());
                        boolean isMatch = m.matches();
                        log.debug("pattern:{},file:{},result:{}",path.getFilename(),x.getName(),isMatch);
                        if(isMatch){
                            logFiles.add(x);
                        }
                    });
                }
            }
        });

        List<String> absolutePaths = new ArrayList<>();
        for(File logFile : logFiles){
            absolutePaths.add(logFile.getCanonicalPath());
        }
        return absolutePaths;
    }

    @ReadOperation
    @ResponseBody
    public Resource retriveLogfile(@Selector String requiredLogFileName) throws IOException {
        log.debug("name:{}",requiredLogFileName);
        File file = new File(requiredLogFileName);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(requiredLogFileName);
        }
        List<String> files = collectNames(this.registry);
        if (!files.contains(requiredLogFileName)){
            throw new FileNotFoundException(requiredLogFileName);
        }
        return new FileSystemResource(requiredLogFileName);
    }

    public static final class ListNamesResponse {

        private final List<String> names;

        ListNamesResponse(List<String> names) {
            this.names = names;
        }

        public List<String> getNames() {
            return this.names;
        }
    }
}
