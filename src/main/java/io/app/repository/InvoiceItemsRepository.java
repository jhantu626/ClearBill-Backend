package io.app.repository;

import io.app.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemsRepository extends JpaRepository<InvoiceItem,Long> {
}
