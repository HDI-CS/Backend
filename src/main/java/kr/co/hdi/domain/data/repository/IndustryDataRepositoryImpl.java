package kr.co.hdi.domain.data.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.hdi.admin.data.dto.response.IndustryDataResponse;
import kr.co.hdi.domain.data.entity.IndustryData;
import kr.co.hdi.domain.data.enums.IndustryDataCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static kr.co.hdi.domain.data.entity.QIndustryData.industryData;
import static kr.co.hdi.domain.data.entity.QIndustryDataImage.industryDataImage;

@RequiredArgsConstructor
public class IndustryDataRepositoryImpl implements IndustryDataRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /*
    이미지가 더 이상 고정 컬럼이 아니라 IndustryDataImage 행이라 QueryDSL DTO projection으로
    바로 못 뽑는다. 엔티티로 fetch join해서 가져온 뒤 기존 getter(IndustryData.getDetailImagePath()
    등)로 조립 — 응답 필드 순서/내용은 기존 동작 그대로 유지.
     */
    @Override
    public List<IndustryDataResponse> search(IndustryDataCategory category, String q) {
        List<IndustryData> results = queryFactory
                .selectFrom(industryData)
                .leftJoin(industryData.images, industryDataImage).fetchJoin()
                .where(
                        keywordContains(q),
                        categoryEq(category),
                        industryData.deletedAt.isNull()
                )
                .distinct()
                .fetch();

        return results.stream()
                .map(i -> new IndustryDataResponse(
                        i.getId(),
                        i.getOriginalId(),
                        i.getProductName(),
                        i.getCompanyName(),
                        i.getPrice(),
                        i.getWeight(),
                        i.getReferenceUrl(),
                        i.getRegisteredAt(),
                        i.getProductPath(),
                        i.getProductTypeName(),
                        i.getUsage(),
                        i.getShoppingUrl(),
                        i.getAttributes(),
                        i.getDetailImagePath(),
                        i.getFrontImagePath(),
                        i.getSideImagePath(),
                        i.getSide2ImagePath(),
                        i.getSide3ImagePath()
                ))
                .toList();
    }

    private BooleanExpression keywordContains(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        // material/modelName은 attributes(jsonb)로 이전되면서 QueryDSL containsIgnoreCase로
        // 바로 검색할 수 없게 됨 — 검색 범위에서 제외.
        return industryData.originalId.containsIgnoreCase(q)
                .or(industryData.companyName.containsIgnoreCase(q))
                .or(industryData.productName.containsIgnoreCase(q))
                .or(industryData.referenceUrl.containsIgnoreCase(q));
    }

    private BooleanExpression categoryEq(IndustryDataCategory category) {
        if (category == null) {
            return null;
        }
        return industryData.industryDataCategory.eq(category);
    }
}
