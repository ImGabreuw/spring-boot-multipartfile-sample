package me.gabreuw.spring_boot_multipartfile_sample.resource;

import me.gabreuw.spring_boot_multipartfile_sample.domain.dto.FileDTO;
import me.gabreuw.spring_boot_multipartfile_sample.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileLocalStorageResource {

    @Autowired
    private FileStorageService service;

    @PostMapping(path = "/single/upload")
    public ResponseEntity<FileDTO> uploadSingleFile(
            @RequestParam MultipartFile file
    ) {
        service.storeFile(file);

        return ResponseEntity
                .ok()
                .body(FileDTO.of(file));
    }

    @GetMapping(path = "/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            HttpServletRequest request
    ) {
        Resource resource = service.downloadFile(fileName);
        MediaType mediaType = service.getMediaTypeFromResource(resource, request);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
//                .header(
//                        HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment;fileName=" + resource.getFilename()
//                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline;fileName=" + resource.getFilename()
                )
                .body(resource);
    }

}
