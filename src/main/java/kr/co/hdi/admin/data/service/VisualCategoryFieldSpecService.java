package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.VisualCategoryFieldSpecRequest;
import kr.co.hdi.admin.data.dto.response.VisualCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.VisualCategoryFieldSpec;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.repository.VisualCategoryFieldSpecRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualCategoryFieldSpecService {

    private final VisualCategoryFieldSpecRepository specRepository;
    private final YearRepository yearRepository;

    public List<VisualCategoryFieldSpecResponse> getSpecs(VisualDataCategory category, Long yearId) {
        List<VisualCategoryFieldSpec> yearSpecific = specRepository.findByCategoryAndYear(category, yearId);
        List<VisualCategoryFieldSpec> specs = yearSpecific.isEmpty()
                ? specRepository.findCommonByCategory(category)
                : yearSpecific;

        return specs.stream()
                .map(VisualCategoryFieldSpecResponse::from)
                .toList();
    }

    // 평가자 화면용. yearId로 좁히지 않고 카테고리의 모든 스펙(연도 전용 + 공통)을 라벨 조회용으로 합쳐서 준다.
    public List<VisualCategoryFieldSpecResponse> getSpecsForDisplay(VisualDataCategory category) {
        return specRepository.findAllByCategory(category).stream()
                .map(VisualCategoryFieldSpecResponse::from)
                .toList();
    }

    @Transactional
    public VisualCategoryFieldSpecResponse createSpec(
            VisualDataCategory category,
            Long yearId,
            VisualCategoryFieldSpecRequest request
    ) {
        Year year = null;
        if (yearId != null) {
            year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                    .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));
        }

        if (specRepository.existsByCategoryAndYearAndFieldKey(category, yearId, request.fieldKey())) {
            throw new DataException(DataErrorCode.DUPLICATE_FIELD_SPEC);
        }

        Integer maxOrder = specRepository.findMaxDisplayOrder(category, yearId);
        int nextOrder = (maxOrder == null ? -1 : maxOrder) + 1;

        VisualCategoryFieldSpec spec = VisualCategoryFieldSpec.create(
                category, year, request.fieldKey(), request.fieldLabel(), nextOrder
        );
        specRepository.save(spec);

        return VisualCategoryFieldSpecResponse.from(spec);
    }
}
