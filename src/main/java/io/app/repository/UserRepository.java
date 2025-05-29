package io.app.repository;

import io.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Update Otp
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.otp=:otp WHERE email=:email")
    int updateOtp(@Param("otp") String otp,@Param("email") String email);
}
