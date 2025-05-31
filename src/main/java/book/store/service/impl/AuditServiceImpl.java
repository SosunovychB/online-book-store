package book.store.service.impl;

import book.store.model.OrderAuditInfo;
import book.store.repository.log.LogRepository;
import book.store.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuditServiceImpl implements AuditService {
    private final LogRepository logRepository;

    @Override
    public void saveOrderAudit(OrderAuditInfo logEntry) {
        logRepository.save(logEntry);
    }
}
