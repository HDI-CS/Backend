package kr.co.hdi.domain.year.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.year.enums.DomainType;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
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

    @Enumerated(EnumType.STRING)
    private DomainType type;

    public static Year create() {
        return new Year();
    }

    public void updateYear(String name) {
        this.year = name;
    }
}
