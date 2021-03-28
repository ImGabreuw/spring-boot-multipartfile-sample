package me.gabreuw.spring_boot_multipartfile_sample.util;

import me.gabreuw.spring_boot_multipartfile_sample.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipHelper {

    @Autowired
    private FileStorageService service;

    public void filesToZip(String[] fileName, HttpServletResponse response) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
            Arrays.stream(fileName).forEach(name -> addFileToZip(
                    zipOutputStream,
                    service.downloadFile(name)
            ));

            zipOutputStream.finish();
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível zipar os arquivos informados");
        }
    }

    private void addFileToZip(ZipOutputStream zipOutputStream, Resource resource) {
        try {
            ZipEntry zipEntry = new ZipEntry(resource.getFilename());

            zipEntry.setSize(resource.contentLength());
            zipOutputStream.putNextEntry(zipEntry);
            StreamUtils.copy(resource.getInputStream(), zipOutputStream);

            zipOutputStream.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException("Ocorreu um erro ao zipar o arquivo " + resource.getFilename() + ".");
        }
    }

}
