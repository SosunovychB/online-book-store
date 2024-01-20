package book.store.repository.book.spec;

import book.store.model.Book;
import book.store.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AuthorSpecificationProvider implements SpecificationProvider<Book> {
    private static final String KEY = "author";

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Specification<Book> getSpecification(String[] authors) {
        return ((root, query, criteriaBuilder) ->
                root.get(KEY).in(Arrays.stream(authors).toArray()));
    }
}
