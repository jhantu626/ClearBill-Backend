package io.app.repository;

import io.app.model.Business;
import io.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business,Long> {
    Business findByUsers(User users);
}
