package com.rabbyte.sbecom.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 3, max = 100, message = "Street name must be at least 5 characters")
    private String streetName;

    @NotBlank
    @Size(min = 3, max = 100, message = "Building name must be at least 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3, max = 100, message = "City name must be at least 5 characters")
    private String city;

    @NotBlank
    @Size(min = 3, max = 100, message = "State must be at least 5 characters")
    private String state;

    @NotBlank
    @Size(min = 3, max = 100, message = "Country name must be at least 5 characters")
    private String country;

    @NotBlank
    private String zipCode;

    @ManyToMany(mappedBy = "addresses")
    @ToString.Exclude
    private List<User> users;

    public Address(String streetName, String buildingName, String city, String state, String country, String zipCode) {
        this.streetName = streetName;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }
}
