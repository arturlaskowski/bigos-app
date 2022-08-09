package com.bigos.restaurant.adapters.restaurant.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RestaurantEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private boolean available;

    private String name;
}
