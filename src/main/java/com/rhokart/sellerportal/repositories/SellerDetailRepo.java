package com.rhokart.sellerportal.repositories;

import com.rhokart.sellerportal.document.SellerDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerDetailRepo extends MongoRepository<SellerDetail, String> {
    Optional<SellerDetail> findByEmail(String email);
}
