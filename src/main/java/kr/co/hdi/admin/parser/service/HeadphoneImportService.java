package kr.co.hdi.admin.parser.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import kr.co.hdi.admin.parser.dto.HeadphoneExcelParser;
import kr.co.hdi.admin.parser.dto.HeadphoneImportRow;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.repository.IndustryDataRepository;
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
public class HeadphoneImportService {

    private final HeadphoneExcelParser parser;
    private final IndustryDataRepository repository;
    private final YearRepository yearRepository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public void importFromLocal(String excelPathStr, String imageRootStr, Long yearId) {

        Path excelPath = Paths.get(excelPathStr);
        Path imageRoot = Paths.get(imageRootStr);

        Year year = yearRepository.findById(yearId)
                .orElseThrow(() -> new IllegalArgumentException("year 없음"));

        List<HeadphoneImportRow> rows = parser.parse(excelPath);

        for (int i = 0; i < rows.size(); i++) {

            HeadphoneImportRow row = rows.get(i);
            if (row.getCode() == null || row.getCode().isBlank()) {
                System.out.println("⚠️ code 없음 skip: " + (i + 2) + "행");
                continue;
            }

            try {
                Path folder = findFolder(imageRoot, row.getCode());

                String code = row.getCode();
                String detailKey = upload(folder, "main.jpg", code, "main");
                String frontKey = upload(folder, "dt.jpg", code, "dt");
                String sideKey = upload(folder, "sub_01.jpg", code, "sub01");
                String side2Key = upload(folder, "sub_02.jpg", code, "sub02");
                String side3Key = upload(folder, "sub_03.jpg", code, "sub03");

                String originalDetail = exists(folder, "main.jpg");
                String originalFront = exists(folder, "dt.jpg");
                String originalSide = exists(folder, "sub_01.jpg");
                String originalSide2 = exists(folder, "sub_02.jpg");
                String originalSide3 = exists(folder, "sub_03.jpg");

                IndustryData entity = IndustryData.createFromImport(
                        year,
                        row,
                        detailKey,
                        frontKey,
                        sideKey,
                        side2Key,
                        side3Key,
                         originalDetail,
                         originalFront,
                         originalSide,
                         originalSide2,
                         originalSide3

                );

                repository.save(entity);
                System.out.println("==== TEST ====");
                System.out.println("code: " + row.getCode());
                System.out.println("folder: " + folder);

            } catch (Exception e) {
                System.out.println("❌ " + (i + 2) + "행 실패");
                e.printStackTrace();
                continue;
            }
        }
    }

    private Path findFolder(Path rootDir, String code) {

        String normalized = String.valueOf(Integer.parseInt(code)); // 0001 → 1

        try {
            return Files.list(rootDir)
                    .filter(Files::isDirectory)
                    .filter(p -> p.getFileName().toString().startsWith(normalized + "_"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("폴더 없음: " + code));

        } catch (Exception e) {
            throw new IllegalStateException("폴더 탐색 실패", e);
        }
    }

    // 이어폰, 헤드셋 등 수정
    private String buildS3Key(String code, String type, String ext) {
        return "2026/ID/PR_" + code + "_eehp_" + type + "." + ext;
    }

    private String upload(Path folder, String fileName, String code, String type) {

        Path path = folder.resolve(fileName);
        if (!Files.exists(path)) return null;

        String ext = getExt(fileName);

        String key = buildS3Key(code, type, ext);

        try (InputStream in = Files.newInputStream(path)) {

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(Files.size(path));

            amazonS3.putObject(bucket, key, in, meta);

            return key;

        } catch (Exception e) {
            throw new IllegalStateException("S3 업로드 실패: " + fileName, e);
        }
    }

    private String exists(Path folder, String fileName) {
        return Files.exists(folder.resolve(fileName)) ? fileName : null;
    }

    private String getExt(String fileName) {
        int idx = fileName.lastIndexOf('.');
        return idx == -1 ? "" : fileName.substring(idx + 1);
    }
}