package org.bigspring.ws;

import org.bigspring.common.MediaFileBean;
import org.bigspring.model.KeyGenEntity;
import org.bigspring.service.KeyGenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(FileController.class.getName());

    @PostMapping("/files/upload")
    @ResponseBody
    public List<MediaFileBean> uploadCardFile(@RequestParam("file") List<MultipartFile> files) {

        List<MediaFileBean> fnames = new ArrayList<>();

        logger.info("### Image folder location - " + imageFolder);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {

                KeyGenEntity key = keyGenRepo.save(new KeyGenEntity());
                Long offset = FILE_OFFSET + key.getId();
                String fname = "IMG" + Long.toString(offset) + ".jpg";

                Path target = Paths.get(imageFolder).resolve(fname);
                try {
                    Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "### Exception while uploading file - " + file.getName(), e);
                    throw new IllegalStateException("Exception while uploading file - " + file.getName(), e);
                }

                var fileBean = new MediaFileBean();
                fileBean.setFileId(offset);
                fileBean.setFileName(fname);
                fileBean.setDownloadLink("/files/download/images/" + fname);

                fnames.add(fileBean);
            }
        }

        return(fnames);
    }

    @GetMapping("files/download/images/{fileName:.+}")
    public ResponseEntity<Resource> downloadMediaFile(@PathVariable("fileName") String fileName) {

        byte[] bytes = null;

        logger.info("### Image folder location - " + imageFolder);

        Path target = Paths.get(imageFolder).resolve(fileName);
        try {
            bytes = Files.readAllBytes(target);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "### Exception while reading file - " + fileName, e);
            throw new IllegalStateException("Exception while reading file - " + fileName, e);
        }

        var headers = new HttpHeaders();
        headers.add(org.springframework.http.HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(org.springframework.http.HttpHeaders.PRAGMA, "no-cache");
        headers.add(org.springframework.http.HttpHeaders.EXPIRES, "0");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        Resource resource = new ByteArrayResource(bytes);
        ResponseEntity<Resource> response = ResponseEntity.ok().headers(headers).body(resource);

        return (response);
    }
}
