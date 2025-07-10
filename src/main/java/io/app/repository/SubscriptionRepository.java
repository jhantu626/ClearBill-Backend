package io.app.repository;

import io.app.dto.projection.SubscriptionProjection;
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

    @Query("SELECT new io.app.dto.projection.SubscriptionProjection(" +
            "s.id,s.subscriptionType,s.purchaseDate,s.expirationDate) FROM " +
            "Subscription s WHERE s.business.id = :businessId AND expirationDate > :time order by s.id desc limit 1")
    SubscriptionProjection findCurrentSubscriptionByBusiness(@Param("businessId") long businessId,
                                                             @Param("time") LocalDateTime time);
}
