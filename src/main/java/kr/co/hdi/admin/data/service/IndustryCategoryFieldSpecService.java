package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.IndustryCategoryFieldSpecRequest;
import kr.co.hdi.admin.data.dto.response.IndustryCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.IndustryCategoryFieldSpec;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.repository.IndustryCategoryFieldSpecRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndustryCategoryFieldSpecService {

    private final IndustryCategoryFieldSpecRepository specRepository;
    private final YearRepository yearRepository;

    /*
    해당 연도에 지정된 스펙이 있으면 그걸 쓰고, 없으면 카테고리 공통(연도 무관) 스펙을 쓴다.
    (Frontend의 INDUSTRY_FIELD_OVERRIDES_BY_YEAR 패턴과 동일한 "연도별 override, 없으면 폴백")
     */
    public List<IndustryCategoryFieldSpecResponse> getSpecs(IndustryDataCategory category, Long yearId) {
        List<IndustryCategoryFieldSpec> yearSpecific = specRepository.findByCategoryAndYear(category, yearId);
        List<IndustryCategoryFieldSpec> specs = yearSpecific.isEmpty()
                ? specRepository.findCommonByCategory(category)
                : yearSpecific;

        return specs.stream()
                .map(IndustryCategoryFieldSpecResponse::from)
                .toList();
    }

    // 평가자 화면용. yearId로 좁히지 않고 카테고리의 모든 스펙(연도 전용 + 공통)을 라벨 조회용으로 합쳐서 준다.
    public List<IndustryCategoryFieldSpecResponse> getSpecsForDisplay(IndustryDataCategory category) {
        return specRepository.findAllByCategory(category).stream()
                .map(IndustryCategoryFieldSpecResponse::from)
                .toList();
    }

    @Transactional
    public IndustryCategoryFieldSpecResponse createSpec(
            IndustryDataCategory category,
            Long yearId,
            IndustryCategoryFieldSpecRequest request
    ) {
        Year year = null;
        if (yearId != null) {
            year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                    .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));
        }

        Integer maxOrder = specRepository.findMaxDisplayOrder(category, yearId);
        int nextOrder = (maxOrder == null ? -1 : maxOrder) + 1;

        IndustryCategoryFieldSpec spec = IndustryCategoryFieldSpec.create(
                category, year, request.fieldKey(), request.fieldLabel(), nextOrder
        );
        specRepository.save(spec);

        return IndustryCategoryFieldSpecResponse.from(spec);
    }
}
