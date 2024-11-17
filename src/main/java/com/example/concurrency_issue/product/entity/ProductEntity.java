package com.example.concurrency_issue.product.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 낙관적 락을 위한 엔티티.
 * version 컬럼이 존재하면 다른 테스트가 불가능해서
 * 낙관적 락 전용 엔티티를 만들었다.
 */
@Entity
@Data
@Table(name = "products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long count;

    // ItemEntity와 다르게 @Version이 추가됨
    @Version
    private Long version;
}
