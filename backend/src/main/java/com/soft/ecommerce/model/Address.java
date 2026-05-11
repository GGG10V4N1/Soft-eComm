package com.soft.ecommerce.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "adresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "building_name")
    private String buildingName;

    private String city;
    private String country;
    private String state;
    private String pincode;
    private String street;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
