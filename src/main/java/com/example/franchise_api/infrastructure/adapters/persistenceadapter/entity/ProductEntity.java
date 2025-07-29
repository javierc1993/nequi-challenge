package com.example.franchise_api.infrastructure.adapters.persistenceadapter.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Data
@Table("products")
public class ProductEntity {
    @Id
    private UUID id;
    private String name;
    private int stock;
    private UUID branchId;
}
