package io.app.repository;

import io.app.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepositoty extends JpaRepository<Invoice,Long> {
}
