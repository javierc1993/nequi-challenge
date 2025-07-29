package com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("franchises")
@Getter
@Setter
@RequiredArgsConstructor
public class FranchiseEntity {
    @Id
    private UUID id;
    private String name;
}
