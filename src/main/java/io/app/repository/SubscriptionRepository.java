package io.app.repository;

import io.app.model.Business;
import io.app.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    @Query(value = "select * from subscription where business_id = :businessId and expiration_date > :dateTime order by id desc limit 1",nativeQuery = true)
    Subscription findByExpirationDateGreaterThanAndBusiness(@Param("dateTime") LocalDateTime dateTime,
                                                            @Param("businessId") long businessId);
}
