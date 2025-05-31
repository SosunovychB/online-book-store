package book.store.repository.log;

import book.store.model.OrderAuditInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends MongoRepository<OrderAuditInfo, String> {
}
