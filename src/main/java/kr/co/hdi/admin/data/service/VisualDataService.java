package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.response.VisualDataIdsResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.admin.data.dto.response.VisualDataWithCategoryResponse;
import kr.co.hdi.admin.data.dto.response.YearResponse;
import kr.co.hdi.admin.data.exception.DataErrorCode;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.VisualData;
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
}
