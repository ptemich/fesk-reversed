package pl.ptemich.ksef.acard;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AcardService {

    private final LocalConfigService localConfigService;

    public AcardService(LocalConfigService localConfigService) {
        this.localConfigService = localConfigService;
    }

    public Set<String> loadAcardList() {
        LocalConfig localConfig = localConfigService.loadFromDisk();

        Path exportPath = Paths.get(localConfig.getExportPath());
        Set<String> inviceXmls = new HashSet<>();

        try (Stream<Path> stream = Files.list(exportPath)) {
            stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    //.map(StringUtils::upperCase)
                    .filter(path -> path.endsWith(".xml"))
                    .map(fileName -> StringUtils.substringBefore(fileName,".xml"))
                    .forEach(inviceXmls::add);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inviceXmls;
    }

}
