package com.blue.bluearchive.admin.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "admin_category_count")
public class CategoryGraph {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categorryGraphId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "category_pre_count")
    private Integer categoryPreCount=0;

    @Column(name = "category_all_count")
    private Integer categoryAllCount=0;
}
