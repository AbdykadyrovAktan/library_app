package kg.mega.library_app.services;

import kg.mega.library_app.models.constants.OrderStatus;
import kg.mega.library_app.models.dto.responses.OrderHistoryResp;
import kg.mega.library_app.models.dto.responses.UserOrderHistoryResp;
import kg.mega.library_app.models.entities.Order;

import java.util.List;

public interface OrderHistoryService {
    void createOrderHistory(Order order, OrderStatus status);

    List<OrderHistoryResp> getOrderHistoryByOrderId(Long orderId);

    List<OrderHistoryResp> getAllOrderHistory();

    List<UserOrderHistoryResp> getUserHistory();
}
