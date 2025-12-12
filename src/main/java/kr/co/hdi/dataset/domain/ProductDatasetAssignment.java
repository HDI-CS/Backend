package kr.co.hdi.dataset.domain;

import jakarta.persistence.*;
import kr.co.hdi.crawl.domain.Product;
import kr.co.hdi.global.domain.BaseTimeEntityWithDeletion;
import kr.co.hdi.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ProductDatasetAssignment extends BaseTimeEntityWithDeletion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id")
    private Product product;


    public ProductDatasetAssignment(UserEntity user, Product product) {
        this.user = user;
        this.product = product;
    }

}
