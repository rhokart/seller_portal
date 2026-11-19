package com.rhokart.sellerportal.repositories;
import com.rhokart.sellerportal.document.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepo extends MongoRepository<RefreshToken, String> {
}
