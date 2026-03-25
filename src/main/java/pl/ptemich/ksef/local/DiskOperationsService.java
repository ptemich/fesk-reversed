package pl.ptemich.ksef.local;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.ptemich.ksef.localconf.LocalConfig;
import pl.ptemich.ksef.localconf.LocalConfigService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class DiskOperationsService {

    private static final Logger log = LoggerFactory.getLogger(DiskOperationsService.class);

    private final LocalConfigService localConfigService;

    public DiskOperationsService(LocalConfigService localConfigService) {
        this.localConfigService = localConfigService;
    }

    public Set<String> listLocalInvoices(InvoiceSource source) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        Path folderPath = Paths.get(resolveFolderPath(localConfig, source));
        Set<String> invoiceXmls = new HashSet<>();

        try (Stream<Path> stream = Files.list(folderPath)) {
            stream.filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    //.map(StringUtils::upperCase)
                    .filter(path -> path.endsWith(".xml"))
                    .map(fileName -> StringUtils.substringBefore(fileName,".xml"))
                    .forEach(invoiceXmls::add);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return invoiceXmls;
    }

    public void saveToDisk(String invoiceNumber, byte[] xmlContent) {
        try {
            LocalConfig localConfig = localConfigService.loadFromDisk();
            File localDirectory = new File(localConfig.getReceivedInvoicesPath());
            File outputFile = new File(localDirectory, invoiceNumber + ".xml");
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

    public byte[] loadFromDisk(InvoiceSource source, String invoiceNumber) {
        LocalConfig localConfig = localConfigService.loadFromDisk();
        String folderPath = resolveFolderPath(localConfig, source);
        Path path = Paths.get(folderPath + "/" + invoiceNumber +  ".xml");
        try {
            byte[] xmlBytes = Files.readAllBytes(path);
            return xmlBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String resolveFolderPath(LocalConfig localConfig, InvoiceSource source) {
        return source == InvoiceSource.RECEIVED ? localConfig.getReceivedInvoicesPath() : localConfig.getGeneratedInvoicesPath();
    }
}
