package com.rhokart.sellerportal.repositories;

import com.rhokart.sellerportal.document.OtpDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OtpDocRepo extends MongoRepository<OtpDoc, String> {
    OtpDoc findByUserKey(String userKey);
}
