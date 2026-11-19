package com.rhokart.sellerportal.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetail {
    private Integer catgId;
    private String catgL1;
    private String catgL2;
    private String catgL3;
    private Boolean isActv;
}
