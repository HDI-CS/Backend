package kr.co.hdi.domain.year.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Year extends BaseTimeEntityWithDeletion {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "year_id")
    private Long id;

    private String year;

    private Integer surveyCount;

    @Enumerated(EnumType.STRING)
    private DomainType type;

    public static Year create(DomainType type) {

        return Year.builder()
                .type(type)
                .build();
    }

    public void updateYear(String name) {
        this.year = name;
    }

    @Builder
    public Year(String year, DomainType type) {
        this.year = year;
        this.type = type;
        this.surveyCount = 0;
    }

    public void updateSurveyCount(int size) {
        this.surveyCount = size;
    }
}
