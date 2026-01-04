package kr.co.hdi.domain.currentSurvey.entity;

import jakarta.persistence.*;
import kr.co.hdi.domain.data.enums.VisualDataCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentVisualCategory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "current_visual_category_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private VisualDataCategory category;
}
