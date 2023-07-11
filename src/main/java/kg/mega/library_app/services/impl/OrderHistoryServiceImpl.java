package kg.mega.library_app.services.impl;

import kg.mega.library_app.dao.OrderHistoryRepo;
import kg.mega.library_app.models.constants.OrderStatus;
import kg.mega.library_app.models.dto.responses.OrderHistoryResp;
import kg.mega.library_app.models.dto.responses.UserOrderHistoryResp;
import kg.mega.library_app.models.entities.Order;
import kg.mega.library_app.models.entities.OrderHistory;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.OrderHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepo orderHistoryRepo;

    @Autowired
    public OrderHistoryServiceImpl(OrderHistoryRepo orderHistoryRepo) {
        this.orderHistoryRepo = orderHistoryRepo;
    }

    @Override
    public void createOrderHistory(Order order, OrderStatus status) {
        orderHistoryRepo.save(OrderHistory
                .builder()
                .order(order)
                .status(status)
                .actionDate(LocalDateTime.now())
                .build());
    }

    @Override
    public List<OrderHistoryResp> getOrderHistoryByOrderId(Long orderId) {
        return orderHistoryRepo.findAllByOrderId(orderId)
                .stream()
                .map(orderHistory -> new OrderHistoryResp(orderHistory.getOrder().getId(),
                        orderHistory.getStatus(),
                        orderHistory.getActionDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderHistoryResp> getAllOrderHistory() {
        return orderHistoryRepo.findAll()
                .stream()
                .map(orderHistory -> new OrderHistoryResp(orderHistory.getOrder().getId(),
                        orderHistory.getStatus(),
                        orderHistory.getActionDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserOrderHistoryResp> getUserHistory() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return orderHistoryRepo.findAllByUserId(user.getId())
                .stream()
                .map(orderHistory -> new UserOrderHistoryResp(
                        orderHistory.getOrder().getUser().getEmail(),
                        orderHistory.getOrder().getBook().getTitle(),
                        orderHistory.getStatus(),
                        orderHistory.getActionDate()
                ))
                .collect(Collectors.toList());
    }
}
