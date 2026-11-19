package com.rhokart.sellerportal.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDetail {
    private String image;
    private String lstName;
    private String childSku;
    private String lstId;
    private String skuId;
    private Integer lstQty = 1;
    private String hsnCode;
    private Integer qtyInStock;
    private Integer unitPriceWoGst;
    private Integer gstRate;
    private Integer gstAmount = 0;
    private Integer unitPriceWGst;
}
