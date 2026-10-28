package kr.co.hdi.admin.data.service;

import kr.co.hdi.admin.data.dto.response.IndustryCategoryFieldSpecResponse;
import kr.co.hdi.admin.data.exception.DataException;
import kr.co.hdi.domain.data.entity.IndustryCategoryFieldSpec;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import kr.co.hdi.domain.data.repository.IndustryCategoryFieldSpecRepository;
import kr.co.hdi.domain.year.repository.YearRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndustryCategoryFieldSpecServiceTest {

    @Mock
    private IndustryCategoryFieldSpecRepository specRepository;

    @Mock
    private YearRepository yearRepository;

    @InjectMocks
    private IndustryCategoryFieldSpecService service;

    @Test
    void getSpecs_prefersYearSpecificSpecsWhenPresent() {
        IndustryCategoryFieldSpec yearSpecific =
                IndustryCategoryFieldSpec.create(IndustryDataCategory.HEADPHONE, null, "maxPlayTime", "최대 재생시간", 0);
        when(specRepository.findByCategoryAndYear(IndustryDataCategory.HEADPHONE, 10L))
                .thenReturn(List.of(yearSpecific));

        List<IndustryCategoryFieldSpecResponse> result = service.getSpecs(IndustryDataCategory.HEADPHONE, 10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fieldKey()).isEqualTo("maxPlayTime");
    }

    @Test
    void getSpecs_fallsBackToCommonSpecsWhenNoYearSpecificExists() {
        when(specRepository.findByCategoryAndYear(IndustryDataCategory.HEADPHONE, 10L))
                .thenReturn(List.of());
        IndustryCategoryFieldSpec common =
                IndustryCategoryFieldSpec.create(IndustryDataCategory.HEADPHONE, null, "codec", "코덱", 0);
        when(specRepository.findCommonByCategory(IndustryDataCategory.HEADPHONE))
                .thenReturn(List.of(common));

        List<IndustryCategoryFieldSpecResponse> result = service.getSpecs(IndustryDataCategory.HEADPHONE, 10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).fieldKey()).isEqualTo("codec");
    }

    @Test
    void createSpec_rejectsDuplicateFieldKeyForSameCategoryAndYear() {
        when(specRepository.existsByCategoryAndYearAndFieldKey(eq(IndustryDataCategory.HEADPHONE), any(), eq("codec")))
                .thenReturn(true);

        var request = new kr.co.hdi.admin.data.dto.request.IndustryCategoryFieldSpecRequest("codec", "코덱");

        assertThatThrownBy(() -> service.createSpec(IndustryDataCategory.HEADPHONE, null, request))
                .isInstanceOf(DataException.class);
    }
}
