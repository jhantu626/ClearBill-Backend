package io.app.repository;

import io.app.model.Business;
import io.app.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepositoty extends JpaRepository<Invoice,Long> {
    Page<Invoice> findByBusiness(Business business, Pageable pageable);
    List<Invoice> findByBusinessAndCreatedAtBetween(Business business,LocalDateTime start,LocalDateTime end);

    // COUNT INVOICES BY BUSINESS AND CREATED AT
    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.business.id=:businessId AND i.createdAt < :start and i.createdAt > :end")
    long countByBusinessAndCreatedAt(@Param("businessId") long businessId,
                                     @Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

}
