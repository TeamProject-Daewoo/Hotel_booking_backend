    package com.example.backend.File;

    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;

    import java.io.File;
    import java.util.*;

    @RestController
    @RequestMapping("/api")
    public class FileUploadController {

        @Value("${file.upload-dir-default}")
        private String defaultUploadDir;

        @Value("${file.upload-dir-alt}")
        private String altUploadDir;

        @PostMapping("/upload")
        public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
            if (files == null || files.length == 0) {
                return ResponseEntity.badRequest().body("업로드할 파일이 없습니다.");
            }

            try {
                // 기본 및 대체 경로
                File dirDefault = new File(defaultUploadDir);
                File dirAlt = new File(altUploadDir);

                String uploadDirToUse;
                if (dirDefault.exists() && dirDefault.canWrite()) {
                    uploadDirToUse = defaultUploadDir;
                } else if (dirAlt.exists() && dirAlt.canWrite()) {
                    uploadDirToUse = altUploadDir;
                } else {
                    dirDefault.mkdirs();
                    if (dirDefault.exists() && dirDefault.canWrite()) {
                        uploadDirToUse = defaultUploadDir;
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("업로드 가능한 경로가 없습니다.");
                    }
                }

                File uploadFolder = new File(uploadDirToUse);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                List<Map<String, String>> uploadedFiles = new ArrayList<>();

                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;

                    String originalFilename = file.getOriginalFilename();
                    String fileName = System.currentTimeMillis() + "_" + originalFilename;

                    File destinationFile = new File(uploadFolder, fileName);
                    file.transferTo(destinationFile);

                    String fileUrl = "/uploads/" + fileName;

                    Map<String, String> fileInfo = new HashMap<>();
                    fileInfo.put("originalName", originalFilename);
                    fileInfo.put("url", fileUrl);
                    uploadedFiles.add(fileInfo);
                }

                return ResponseEntity.ok(uploadedFiles);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("파일 업로드 실패: " + e.getMessage());
            }
        }
    }
