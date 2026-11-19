package com.rhokart.sellerportal.service;

import com.rhokart.sellerportal.models.CategoryDetail;
import com.rhokart.sellerportal.models.InventoryDetail;
import com.rhokart.sellerportal.repositories.CategoryRepository;
import com.rhokart.sellerportal.repositories.InventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class InventoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<String> getCategories(String level,String parentValue) {
        List<CategoryDetail> categoryDetails = categoryRepository.getAllCategoryDetails();
        log.info(categoryDetails.toString());
        switch (level) {
            case "L1":
                return categoryDetails.stream()
                        .map(CategoryDetail::getCatgL1)
                        .filter(Objects::nonNull).distinct().collect(Collectors.toList());
            case "L2":
                return categoryDetails.stream()
                        .filter(categoryDetail -> categoryDetail.getCatgL1().equals(parentValue))
                        .map(CategoryDetail::getCatgL2)
                        .filter(Objects::nonNull).distinct().collect(Collectors.toList());
            case "L3":
                return categoryDetails.stream()
                        .filter(categoryDetail -> categoryDetail.getCatgL2().equals(parentValue))
                        .map(CategoryDetail::getCatgL3)
                        .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<InventoryDetail> getInventory(String categoryL1Id, String categoryL2Id, String categoryL3Id, String sellerId,int size, int offset) {
        log.info("Limit :: {} and Offset :: {}", size, offset);

        List<InventoryDetail> sellerInventory =  inventoryRepository.findByFilters(
                categoryL1Id, categoryL2Id, categoryL3Id, sellerId, size, offset);
        log.info("Inventory :: {}", sellerInventory);
        return sellerInventory;
    }

    @Transactional
    public boolean updateInventory(
            String skuId,
            Integer hsnCode,
            Integer qtyInStock,
            Integer unitPriceWoGst,
            Integer gstRate,
            Integer gstAmount,
            Integer unitPriceWGst) {
        try {
            // Call the mapper to update inventory details
            boolean isInventoryUpdated =  inventoryRepository.updateInventoryDetails(
                    skuId,
                    qtyInStock,
                    unitPriceWoGst,
                    gstRate,
                    unitPriceWGst,
                    gstAmount
            );
            boolean isHsnUpdated =  inventoryRepository.updateSkuDetails(
                    skuId,
                    hsnCode
            );
            return isInventoryUpdated || isHsnUpdated;
        } catch (Exception e) {
            log.error("Error updating inventory: {}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
