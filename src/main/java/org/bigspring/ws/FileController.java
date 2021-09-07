package org.bigspring.ws;

import org.bigspring.model.KeyGenEntity;
import org.bigspring.service.KeyGenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class FileController {

    @Value("${app.file-upload.image-folder}")
    private String imageFolder;

    @Autowired
    @Qualifier("keyGenRepository")
    private KeyGenRepository keyGenRepo;

    private static final Long FILE_OFFSET = 100000L;

    @PostMapping("/files/upload")
    @ResponseBody
    public List<String> uploadCardFile(@RequestParam("file") List<MultipartFile> files) {

        List<String> fnames = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                KeyGenEntity key = keyGenRepo.save(new KeyGenEntity());
                Long offset = FILE_OFFSET + key.getId();
                String fname = "IMG" + Long.toString(offset) + ".jpg";

                Path target = Paths.get(imageFolder).resolve(fname);
                try {
                    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IllegalStateException("Exception while uploading file - " + file.getName());
                }
            }
        }

        return(fnames);
    }
}
