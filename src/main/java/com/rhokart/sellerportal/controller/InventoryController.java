package com.rhokart.sellerportal.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.rhokart.sellerportal.models.InventoryDetail;
import com.rhokart.sellerportal.models.SubmitResponse;
import com.rhokart.sellerportal.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "pages")
@Slf4j
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @Value("${application.aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public InventoryController(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping(value = "inventory")
    public ModelMap inventoryDashboard() {
        ModelMap map = new ModelMap();
        map.addAttribute("userName","Seller");
        return map;
    }

    @GetMapping(value = "excel-template")
    public ModelMap templateDashboard() {
        ModelMap map = new ModelMap();
        map.addAttribute("userName","Seller");
        return map;
    }

    @GetMapping("/api/inventory")
    public ResponseEntity<List<InventoryDetail>> getInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryL1Id,
            @RequestParam(required = false) String categoryL2Id,
            @RequestParam(required = false) String categoryL3Id,
            @RequestParam(required = false) String sellerId) {
        try {
            int offset = (page) * size;
            List<InventoryDetail> inventory = inventoryService.getInventory(categoryL1Id, categoryL2Id, categoryL3Id, "000001", size, offset);
            return ResponseEntity.ok(inventory);
        } catch (Exception e) {
            log.error("Error fetching inventory: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/api/submit")
    public ResponseEntity<SubmitResponse> submitInventoryChanges(
            @RequestParam String skuId,
            @RequestParam Integer hsnCode,
            @RequestParam Integer qtyInStock,
            @RequestParam Integer unitPriceWoGst,
            @RequestParam Integer gstRate,
            @RequestParam Integer gstAmount,
            @RequestParam Integer unitPriceWGst) {
        try {
            // Validation checks
            if (skuId == null || skuId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "Sku Id cannot be empty"));
            }

            if (hsnCode == null || hsnCode < 0) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "HSN Code must be a non-negative number"));
            }

            if (unitPriceWoGst == null || unitPriceWoGst < 0) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "Unit Price without GST must be a non-negative number"));
            }

            if (gstRate == null || gstRate < 0) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "GST Rate must be a non-negative number"));
            }

            if (gstAmount == null || gstAmount < 0) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "GST Amount must be a non-negative number"));
            }

            if (unitPriceWGst == null || unitPriceWGst < 0) {
                return ResponseEntity.badRequest()
                        .body(new SubmitResponse(false, "Unit Price with GST must be a non-negative number"));
            }

            boolean updated = inventoryService.updateInventory(
                    skuId, hsnCode,qtyInStock, unitPriceWoGst, gstRate, gstAmount, unitPriceWGst);

            if (updated) {
                return ResponseEntity.ok(new SubmitResponse(true, "Inventory updated successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new SubmitResponse(false, "SKU not found"));
            }
        } catch (Exception e) {
            log.error("Error updating inventory: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new SubmitResponse(false, "Error updating inventory"));
        }
    }

    @GetMapping("/api/applicable/category")
    public ResponseEntity<?> getApplicableCategories(
            @RequestParam String level,
            @RequestParam(required = false) String parent) {

        // Validate the level parameter
        if (!level.equals("L1") && !level.equals("L2") && !level.equals("L3")) {
            return ResponseEntity.badRequest().body("Invalid level parameter. Must be L1, L2, or L3.");
        }

        List<String> categories = new ArrayList<>();
        categories = inventoryService.getCategories(level,parent);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/api/download-excel")
    public ResponseEntity<?> downloadExcel(
            @RequestParam String l1,
            @RequestParam String l2,
            @RequestParam String category) {

        String fileName = l2.concat("/").concat(l2).concat(" - ").concat(category).concat(".xlsx");
        log.info("Excel file name : {}", fileName);

        if (!s3Client.doesObjectExist(bucketName, fileName)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Template not available for ".concat(category));
        }

        // Generate a pre-signed URL instead of streaming the file through the server
        Date expiration = new Date(System.currentTimeMillis() + 60000); // URL expires in 1 hour
        URL presignedUrl = s3Client.generatePresignedUrl(bucketName, fileName, expiration);

        return ResponseEntity.ok(presignedUrl.toString());
    }

    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam String categoryL1,
            @RequestParam String categoryL2) throws IOException {

        String fileName = categoryL1 + "_" + categoryL2 + "_" + file.getOriginalFilename();

        // Validate Excel file
        try (Workbook ignored = new HSSFWorkbook(file.getInputStream())) {
            // Perform any necessary validation here
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid Excel file");
        }

        // Upload to S3
        s3Client.putObject(bucketName, fileName, file.getInputStream(), null);

        return ResponseEntity.ok("File uploaded successfully");
    }
}
