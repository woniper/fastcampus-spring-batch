package fastcampus.spring.batch;

import fastcampus.spring.batch.part5.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Long> {
}
