package book.store.service;

import book.store.model.OrderAuditInfo;

public interface AuditService {
    void saveOrderAudit(OrderAuditInfo logEntry);
}
