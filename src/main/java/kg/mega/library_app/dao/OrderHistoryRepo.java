package kg.mega.library_app.dao;

import kg.mega.library_app.models.entities.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepo extends JpaRepository<OrderHistory, Long> {
    @Query("SELECT oh FROM OrderHistory oh WHERE oh.order.id = ?1")
    List<OrderHistory> findAllByOrderId(Long orderId);
    @Query("SELECT oh FROM  OrderHistory oh WHERE oh.order.user.id = ?1")
    List<OrderHistory> findAllByUserId(Long userId);
}
