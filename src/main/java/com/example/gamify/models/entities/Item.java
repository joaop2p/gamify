package com.example.gamify.models.entities;

import com.example.gamify.models.dtos.output.ItemOutputDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Item {
    @UuidGenerator
    @Id
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(nullable = false)
    private int value;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Getter
    private LocalDateTime created_at = LocalDateTime.now();

    @Setter
    @Getter
    private LocalDateTime updated_at = LocalDateTime.now();

    @Getter
    @Setter
    @Column(nullable = true)
    private LocalDateTime purchase_at;

    public  Item() {
    }

    public Item(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public ItemOutputDTO toOutputDTO() {
        return new ItemOutputDTO(id, name, value, created_at, updated_at, purchase_at);
    }
}
