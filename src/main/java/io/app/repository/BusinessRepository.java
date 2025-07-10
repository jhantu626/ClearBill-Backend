package io.app.repository;

import io.app.model.Business;
import io.app.model.SubscriptionType;
import io.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<Business,Long> {
    Business findByUsers(User users);
}
