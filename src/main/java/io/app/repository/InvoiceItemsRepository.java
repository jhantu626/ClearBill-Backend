package io.app.repository;

import io.app.model.Business;
import io.app.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface InvoiceItemsRepository extends JpaRepository<InvoiceItem,Long> {
}
