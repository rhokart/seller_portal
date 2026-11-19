package com.rhokart.sellerportal.repositories.Queries;

public class Query {
    public static final String GET_ALL_INVENTORY = "SELECT sku_id, sku_nme, seller_id, seller_nme, units, gst, is_actv, unit_price_with_gst, unit_price_wo_gst, currency, unit_price_with_gst_with_commission, unit_price_wo_gst_with_commission\n" +
            "FROM product.inventory_dtl limit 50";

    public static final String GET_SELLER_INVENTORY = "select lst.prnt_lst_name,sku.child_lst_nme,lst.lst_id,sku.sku_id,inv.units,inv.unit_price_wo_gst,inv.gst_pct,inv.unit_price_with_gst,sku.hsn_cde,inv.gst_amount from product.catg_dtl catg, product.lst_dtl lst , product.sku_dtl sku , product.inventory_dtl  inv\n" +
            "   where sku.lst_id = lst.lst_id \n" +
            "     and lst.catg_id = catg.catg_id\n" +
            "     and sku.sku_id = inv.sku_id\n" +
            "     and catg.catg_l1 = #{categoryL1Id}\n" +
            "     and catg.catg_l2 = #{categoryL2Id}\n" +
            "     and catg.catg_l3 = #{categoryL3Id}\n" +
            "     and inv.seller_id = #{sellerId}\n";

    public static final String UPDATE_SKU_DETAIL_INVENTORY = "UPDATE product.sku_dtl sku\n" +
            "SET\n" +
            "hsn_cde = #{hsnCode}\n" +
            "WHERE sku.sku_id = #{skuId};";

    public static final String UPDATE_INVENTORY_DETAIL = "UPDATE product.inventory_dtl inv\n" +
            "SET\n" +
            "unit_price_wo_gst = #{unitPriceWoGst},\n" +
            "units = #{qtyInStock},\n" +
            "gst_pct = #{gstRate},\n" +
            "gst_amount = #{gstAmount},\n" +
            "unit_price_with_gst = #{unitPriceWGst}\n" +
            "WHERE inv.sku_id = #{skuId};";
}

