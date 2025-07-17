package gift.repository;

import gift.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedIsFalse(Long id);

    Page<Product> findAllByDeletedIsFalseAndValidated(Boolean validated, Pageable pageable);
}
