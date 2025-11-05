package kr.co.hdi.dataset.service;

import kr.co.hdi.crawl.domain.Product;
import kr.co.hdi.crawl.repository.ProductRepositoryCustom;
import kr.co.hdi.dataset.domain.Brand;
import kr.co.hdi.dataset.domain.BrandDatasetAssignment;
import kr.co.hdi.dataset.domain.ProductDatasetAssignment;
import kr.co.hdi.dataset.repository.BrandDatasetAssignmentRepository;
import kr.co.hdi.dataset.repository.BrandRepository;
import kr.co.hdi.dataset.repository.ProductDatasetAssignmentRepository;
import kr.co.hdi.user.domain.UserEntity;
import kr.co.hdi.user.domain.UserType;
import kr.co.hdi.user.exception.AuthErrorCode;
import kr.co.hdi.user.exception.AuthException;
import kr.co.hdi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatasetAssignService {

    private final UserRepository userRepository;

    private final ProductRepositoryCustom productRepositoryCustom;
    private final ProductDatasetAssignmentRepository productDatasetAssignmentRepository;

    private final BrandRepository brandRepository;
    private final BrandDatasetAssignmentRepository brandDatasetAssignmentRepository;

    @Transactional
    public void matchUserAndData(Long userId, String filePath) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.USER_NOT_FOUND));

        if (user.getUserType() == UserType.PRODUCT) {
            assignProductDesignDataToUser(user, filePath);
        }
        if (user.getUserType() == UserType.BRAND) {
            assignBrandDesignDataToUser(user, filePath);
        }
    }

    private void assignProductDesignDataToUser(UserEntity user, String filePath) {

        // 파일 읽어서 배정
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            List<ProductDatasetAssignment> productDatasetAssignments = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (isHeader) { // 첫 줄이 헤더면 건너뜀
                    isHeader = false;
                    continue;
                }

                // CSV 구분자 기준으로 split
                String[] columns = line.split(",");

                // CSV 형식이 제일 앞 줄에 id가 오는 경우
                Long originalId = Long.parseLong(columns[0].trim());

                // 새로운 배정 엔티티 생성
                Product product = productRepositoryCustom.findByOriginalId(originalId)
                        .orElseThrow(() -> new IllegalArgumentException("Product not found for originalId: " + originalId));
                productDatasetAssignments.add(new ProductDatasetAssignment(user, product));

            }
            productDatasetAssignmentRepository.saveAll(productDatasetAssignments);

        } catch (IOException e) {
            throw new RuntimeException("CSV 파일을 읽는 중 오류가 발생했습니다: " + filePath, e);
        } catch (Exception e) {
            throw new RuntimeException("데이터 배정 중 오류가 발생했습니다.", e);
        }
    }

    private void assignBrandDesignDataToUser(UserEntity user, String filePath) {

        // 파일 읽어서 배정
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            List<BrandDatasetAssignment> brandDatasetAssignments = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                if (isHeader) { // 첫 줄이 헤더면 건너뜀
                    isHeader = false;
                    continue;
                }

                // CSV 구분자 기준으로 split
                String[] columns = line.split(",");

                // CSV 형식이 제일 앞 줄에 id가 오는 경우
                String brandCode = columns[0].trim();

                // 새로운 배정 엔티티 생성
                Brand brand = brandRepository.findByBrandCode(brandCode)
                        .orElseThrow(() -> new IllegalArgumentException("Brand not found for code: " + brandCode));
                brandDatasetAssignments.add(new BrandDatasetAssignment(user, brand));

            }
            brandDatasetAssignmentRepository.saveAll(brandDatasetAssignments);

        } catch (IOException e) {
            throw new RuntimeException("CSV 파일을 읽는 중 오류가 발생했습니다: " + filePath, e);
        } catch (Exception e) {
            throw new RuntimeException("데이터 배정 중 오류가 발생했습니다.", e);
        }
    }
}
