package com.chuwa.orderservice.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {


    private Long addressId;

    private String street;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private AddressType type;

    @JsonProperty("isDefault")
    private boolean isDefault;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    enum AddressType {
        SHIPPING, BILLING
    }

}
