package com.rhokart.sellerportal.repositories;

import com.rhokart.sellerportal.models.InventoryDetail;
import com.rhokart.sellerportal.repositories.Queries.Query;
import org.apache.ibatis.annotations.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface InventoryRepository {
    @Select(Query.GET_ALL_INVENTORY)
    @Results({
            @Result(property = "unitPriceWithGst", column = "unit_price_with_gst"),
            @Result(property = "unitPriceWoGst", column = "unit_price_wo_gst"),
            @Result(property = "unitPriceWithGstWithCommission", column = "unit_price_with_gst_with_commission"),
            @Result(property = "currency", column = "currency"),
            @Result(property = "units", column = "units"),
            @Result(property = "gst", column = "gst"),
            @Result(property = "unitPriceWoGstWithCommission", column = "unit_price_wo_gst_with_commission"),
            @Result(property = "skuNme", column = "sku_nme"),
            @Result(property = "isActv", column = "is_actv"),
            @Result(property = "skuId", column = "sku_id"),
            @Result(property = "sellerId", column = "seller_id"),
            @Result(property = "sellerNme", column = "seller_nme")
    })
    List<InventoryDetail> getAllInventoryDetails();

    @Select(Query.GET_SELLER_INVENTORY)
    @Results({
            @Result(property = "lstName", column = "prnt_lst_name"),
            @Result(property = "childSku", column = "child_lst_nme"),
            @Result(property = "lstId", column = "lst_id"),
            @Result(property = "skuId", column = "sku_id"),
            @Result(property = "qtyInStock", column = "units"),
            @Result(property = "unitPriceWoGst", column = "unit_price_wo_gst"),
            @Result(property = "gstRate", column = "gst_pct"),
            @Result(property = "gstAmount", column = "gst_amount"),
            @Result(property = "unitPriceWGst", column = "unit_price_with_gst"),
            @Result(property = "hsnCode", column = "hsn_cde")
    })
    List<InventoryDetail> findByFilters(String categoryL1Id, String categoryL2Id, String categoryL3Id, String sellerId,@Param("limit") int limit,@Param("offset") int offset);

    @Update(Query.UPDATE_SKU_DETAIL_INVENTORY)
    boolean updateSkuDetails(
            @Param("skuId") String skuId,
            @Param("hsnCode") Integer hsnCode
    );
    @Update(Query.UPDATE_INVENTORY_DETAIL)
    boolean updateInventoryDetails(
            @Param("skuId") String skuId,
            @Param("qtyInStock") Integer qtyInStock,
            @Param("unitPriceWoGst") Integer unitPriceWoGst,
            @Param("gstRate") Integer gstRate,
            @Param("unitPriceWGst") Integer unitPriceWGst,
            @Param("gstAmount") Integer gstAmount
    );
}
