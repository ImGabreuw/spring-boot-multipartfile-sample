package me.gabreuw.spring_boot_multipartfile_sample.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.Serializable;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@Getter
public class FileDTO implements Serializable {

    private String fileName;
    private String contentType;
    private String url;

    public static FileDTO of(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        return new FileDTO(
                fileName,
                file.getContentType(),
                url
        );
    }
}
