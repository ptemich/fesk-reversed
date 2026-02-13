package pl.ptemich.ksef.acard;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.gov.crd.wzor._2025._06._25._13775.Faktura;
import pl.ptemich.ksef.ksef.AuthorizedKsefService;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AcardService {

    private static final Logger log = LoggerFactory.getLogger(AcardService.class);

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

    public void save(String ksefNumber, byte[] xmlContent) {
        try {
            LocalConfig localConfig = localConfigService.loadFromDisk();
            File acardDirectory = new File(localConfig.getExportPath());
            File outputFile = new File(acardDirectory, ksefNumber + ".xml");
            outputFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(xmlContent);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            log.error("Failed to save invoice", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Failed to save invoice", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] load(String ksefNumber) {
        LocalConfig localConfig = localConfigService.loadFromDisk();

        Path path = Paths.get(localConfig.getExportPath() + "/" + ksefNumber +  ".xml");
        try {
            byte[] xmlBytes = Files.readAllBytes(path);
            return xmlBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
