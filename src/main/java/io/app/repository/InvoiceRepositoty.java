package io.app.repository;

import io.app.model.Business;
import io.app.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepositoty extends JpaRepository<Invoice,Long> {
    Page<Invoice> findByBusiness(Business business, Pageable pageable);
}
