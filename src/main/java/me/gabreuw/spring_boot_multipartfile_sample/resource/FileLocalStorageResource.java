package me.gabreuw.spring_boot_multipartfile_sample.resource;

import me.gabreuw.spring_boot_multipartfile_sample.domain.dto.FileDTO;
import me.gabreuw.spring_boot_multipartfile_sample.service.FileStorageService;
import me.gabreuw.spring_boot_multipartfile_sample.util.ZipHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileLocalStorageResource {

    @Autowired
    private FileStorageService service;
    @Autowired
    private ZipHelper zipHelper;

    @PostMapping(path = "/single/upload")
    public ResponseEntity<FileDTO> uploadSingleFile(
            @RequestParam MultipartFile file
    ) {
        service.storeFile(file);

        return ResponseEntity
                .ok()
                .body(FileDTO.of(file));
    }

    @PostMapping(path = "/multiple/upload")
    public ResponseEntity<List<FileDTO>> uploadMultipleFile(
            @RequestParam MultipartFile[] files
    ) {
        if (files.length > 7) {
            throw new RuntimeException("Muitos arquivos. Por favor insira no máximo 7 arquivos por vez.");
        }

        List<FileDTO> fileDTOList = Arrays.stream(files)
                .map(this::saveAndToDTO)
                .collect(Collectors.toList());

        return ResponseEntity
                .ok()
                .body(fileDTOList);
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
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline;fileName=" + resource.getFilename()
                )
                .body(resource);
    }

    @GetMapping(path = "/download/zip")
    public ResponseEntity<Void> downloadMultipleFiles(
            @RequestParam String[] fileName,
            HttpServletResponse response
    ) {
        zipHelper.filesToZip(fileName, response);

        return ResponseEntity
                .ok()
                .build();
    }

    private FileDTO saveAndToDTO(MultipartFile file) {
        service.storeFile(file);

        return FileDTO.of(file);
    }

}
