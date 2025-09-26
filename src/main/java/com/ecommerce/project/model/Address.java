package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Data
@Table(name = "addresses")
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must contain atleast 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name must contain atleast 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 3, message = "City name must contain atleast 3 characters")
    private String city;

    @NotBlank
    @Size(min = 4, message = "State name must contain atleast 4 characters")
    private String state;

    @NotBlank
    @Size(min = 3, message = "Country name must contain atleast 4 characters")
    private String country;

    @NotBlank
    @Size(min = 5, message = "Pincode must contain atleast 5 characters")
    private String pincode;

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
    }


    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
