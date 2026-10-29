package kr.co.hdi.domain.data.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.data.dto.response.VisualDataResponse;
import kr.co.hdi.domain.data.entity.VisualData;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.data.entity.QVisualData.visualData;
import static kr.co.hdi.domain.data.entity.QVisualDataImage.visualDataImage;

@RequiredArgsConstructor
public class VisualDataRepositoryImpl implements VisualDataRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /*
    이미지가 더 이상 고정 컬럼이 아니라 VisualDataImage 행이라 QueryDSL DTO projection으로
    바로 못 뽑는다. IndustryDataRepositoryImpl.search()와 동일한 패턴 — 엔티티로 fetch join해서
    가져온 뒤 기존 getter(VisualData.getLogoImage() 등)로 조립.
     */
    @Override
    public List<VisualDataResponse> search(String q, VisualDataCategory category) {

        List<VisualData> results = queryFactory
                .selectFrom(visualData)
                .leftJoin(visualData.images, visualDataImage).fetchJoin()
                .where(
                        keywordContains(q),
                        categoryEq(category),
                        visualData.deletedAt.isNull()
                )
                .distinct()
                .fetch();

        return results.stream()
                .map(v -> new VisualDataResponse(
                        v.getId(),
                        v.getBrandCode(),
                        v.getBrandName(),
                        v.getSectorCategory(),
                        v.getReferenceUrl(),
                        v.getAttributes(),
                        v.getLogoImage()
                ))
                .toList();
    }

    private BooleanExpression keywordContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        // mainProductCategory/mainProduct/target은 attributes(jsonb)로 이전되면서
        // QueryDSL containsIgnoreCase로 바로 검색할 수 없게 됨 — 검색 범위에서 제외.
        return visualData.brandCode.containsIgnoreCase(q)
                .or(visualData.brandName.containsIgnoreCase(q))
                .or(visualData.sectorCategory.containsIgnoreCase(q))
                .or(visualData.referenceUrl.containsIgnoreCase(q));
    }

    private BooleanExpression categoryEq(VisualDataCategory category) {
        if (category == null) {
            return null;
        }
        return visualData.visualDataCategory.eq(category);
    }
}
