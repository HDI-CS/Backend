package kr.co.hdi.admin.parser.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kr.co.hdi.admin.parser.dto.VisualExcelParser;
import kr.co.hdi.admin.parser.dto.VisualImportRow;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VisualImportService {

    private final VisualExcelParser parser;
    private final VisualDataRepository repository;
    private final YearRepository yearRepository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void importFromLocal(String excelPathStr, String imageDirStr, Long yearId) {

        Path excelPath = Paths.get(excelPathStr);
        Path imageDir = Paths.get(imageDirStr);

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("year 없음"));

        List<VisualImportRow> rows = parser.parse(excelPath);


        for (int i = 0; i < rows.size(); i++) {
            VisualImportRow row = rows.get(i);
              try {
                validate(row, i + 2);

                Path imagePath = resolveImage(imageDir, row.getOriginalLogoImage());

                String s3Key = upload(imagePath, row.getOriginalLogoImage());

                VisualData entity = VisualData.createFromImport(
                        year,
                        row,
                        s3Key
                );

                repository.save(entity);

            } catch (Exception e) {
                throw new IllegalStateException((i + 2) + "행 실패", e);
            }
        }
    }

    private void validate(VisualImportRow row, int line) {
        if (row.getOriginalLogoImage().isBlank()) {
            throw new IllegalArgumentException(line + "행: 이미지 없음");
        }
        if (row.getReferenceUrl().isBlank()) {
            throw new IllegalArgumentException(line + "행: URL 없음");
        }
    }

    private Path resolveImage(Path dir, String fileName) {
        Path path = dir.resolve(fileName);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("이미지 없음: " + fileName);
        }
        return path;
    }

    private String upload(Path imagePath, String originalFileName) {

        String key = "2026/VI/" + originalFileName;

        try (InputStream in = Files.newInputStream(imagePath)) {

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(Files.size(imagePath));

            amazonS3.putObject(bucket, key, in, meta);

            return key;

        } catch (Exception e) {
            throw new IllegalStateException("S3 업로드 실패", e);
        }
    }

    private String getExt(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx == -1 ? "" : fileName.substring(idx + 1);
    }
}