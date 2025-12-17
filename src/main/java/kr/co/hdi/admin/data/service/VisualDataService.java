package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.request.VisualDataRequest;
import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataWithCategoryResponse;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import kr.co.hdi.domain.data.repository.VisualDataRepository;
import kr.co.hdi.domain.year.entity.Year;
import kr.co.hdi.domain.year.repository.YearRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisualDataService {

    private final YearRepository yearRepository;
    private final VisualDataRepository visualDataRepository;

    /*
    시각 디자인 연도 목록 조회
     */
    public List<YearResponse> getVisualDataYears() {

        List<Year> years = yearRepository.findAll();
        return years.stream()
                .map(YearResponse::from)
                .toList();
    }

    /*
    시각 디자인 데이터셋 리스트 조회
     */
    public List<VisualDataWithCategoryResponse> getVisualDataList(@PathVariable Long yearId) {

        List<VisualData> visualDatas = visualDataRepository.findByYearIdAndDeletedAtIsNull(yearId);

        return visualDatas.stream()
                .collect(Collectors.groupingBy(VisualData::getVisualDataCategory))
                .entrySet()
                .stream()
                .map(entry -> new VisualDataWithCategoryResponse(
                        entry.getKey().name(),
                        entry.getValue().stream()
                                .map(VisualDataResponse::from)
                                .toList()
                ))
                .toList();
    }

    /*
    시각 디자인 데이터셋 조회
     */
    public VisualDataResponse getVisualData(@PathVariable Long datasetId) {

        VisualData visualData = visualDataRepository.findById(datasetId)
                .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        return VisualDataResponse.from(visualData);
    }

    /*
    시각 전문가에게 매칭할 데이터셋 후보 조회
     */
    public List<VisualDataIdsResponse> getVisualDataIds(@PathVariable Long yearId) {

        return visualDataRepository.findIdByYearId(yearId);
    }

    /*
    시각 디자인 데이터셋 복제
     */
    @Transactional
    public void duplicateVisualData(List<Long> ids) {

        List<VisualData> visualDatas = visualDataRepository.findByIdInAndDeletedAtIsNull(ids);
        List<VisualData> duplicated = visualDatas.stream()
                .map(VisualData::duplicate)
                .toList();
        visualDataRepository.saveAll(duplicated);
    }

    /*
    시각 디자인 데이터셋 생성
     */
    @Transactional
    public void createVisualData(Long yearId, VisualDataRequest request) {

        Year year = yearRepository.findByIdAndDeletedAtIsNull(yearId)
                .orElseThrow(() -> new DataException(DataErrorCode.YEAR_NOT_FOUND));

        VisualData visualData = VisualData.create(year, request);
        visualDataRepository.save(visualData);
    }

    /*
    시각 디자인 데이터셋 수정
     */
    @Transactional
    public void updateVisualData(Long datasetId, VisualDataRequest request) {

        VisualData visualData = visualDataRepository.findByIdAndDeletedAtIsNull(datasetId)
                        .orElseThrow(() -> new DataException(DataErrorCode.DATA_NOT_FOUND));

        visualData.updatePartial(request);
        visualDataRepository.save(visualData);
    }

    /*
    시각 디자인 데이터셋 삭제
     */
    @Transactional
    public void deleteVisualData(List<Long> ids) {

        List<VisualData> visualDatas = visualDataRepository.findByIdInAndDeletedAtIsNull(ids);
        visualDatas.forEach(VisualData::delete);
    }

    /*
    시각 디자인 데이터셋 검색
     */
    public List<VisualDataResponse> searchVisualData(String q, VisualDataCategory category) {

        return visualDataRepository.search(q, category);
    }
}
