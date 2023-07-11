package kg.mega.library_app.services;

import kg.mega.library_app.dao.OrderHistoryRepo;
import kg.mega.library_app.models.constants.OrderStatus;
import kg.mega.library_app.models.dto.responses.OrderHistoryResp;
import kg.mega.library_app.models.entities.Order;
import kg.mega.library_app.models.entities.OrderHistory;
import kg.mega.library_app.services.impl.OrderHistoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class OrderHistoryServiceImplTest {
    private OrderHistoryServiceImpl orderHistoryService;

    @Mock
    private OrderHistoryRepo orderHistoryRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderHistoryService = new OrderHistoryServiceImpl(orderHistoryRepo);
    }

    @Test
    void testCreateOrderHistory() {
        Order order = Order.builder().id(1L).build();
        OrderStatus status = OrderStatus.TAKING;

        orderHistoryService.createOrderHistory(order, status);

        verify(orderHistoryRepo, times(1)).save(any(OrderHistory.class));
    }

    @Test
    void testGetOrderHistoryByOrderId() {
        Long orderId = 1L;

        List<OrderHistory> orderHistories = new ArrayList<>();
        OrderHistory orderHistory1 = OrderHistory.builder()
                .order(Order.builder().id(orderId).build())
                .status(OrderStatus.TAKING)
                .actionDate(LocalDateTime.now())
                .build();
        orderHistories.add(orderHistory1);

        OrderHistory orderHistory2 = OrderHistory.builder()
                .order(Order.builder().id(orderId).build())
                .status(OrderStatus.RETURNING)
                .actionDate(LocalDateTime.now())
                .build();
        orderHistories.add(orderHistory2);

        when(orderHistoryRepo.findAllByOrderId(orderId)).thenReturn(orderHistories);

        List<OrderHistoryResp> result = orderHistoryService.getOrderHistoryByOrderId(orderId);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(orderId, result.get(0).getOrderId());
        Assertions.assertEquals(OrderStatus.TAKING, result.get(0).getStatus());
        Assertions.assertEquals(orderId, result.get(1).getOrderId());
        Assertions.assertEquals(OrderStatus.RETURNING, result.get(1).getStatus());

        verify(orderHistoryRepo, times(1)).findAllByOrderId(orderId);
    }

    @Test
    void testGetAllOrderHistory() {
        List<OrderHistory> orderHistories = new ArrayList<>();
        OrderHistory orderHistory1 = OrderHistory.builder()
                .order(Order.builder().id(1L).build())
                .status(OrderStatus.TAKING)
                .actionDate(LocalDateTime.now())
                .build();
        orderHistories.add(orderHistory1);

        OrderHistory orderHistory2 = OrderHistory.builder()
                .order(Order.builder().id(2L).build())
                .status(OrderStatus.RETURNING)
                .actionDate(LocalDateTime.now())
                .build();
        orderHistories.add(orderHistory2);

        when(orderHistoryRepo.findAll()).thenReturn(orderHistories);

        List<OrderHistoryResp> result = orderHistoryService.getAllOrderHistory();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(orderHistory1.getOrder().getId(), result.get(0).getOrderId());
        Assertions.assertEquals(OrderStatus.TAKING, result.get(0).getStatus());
        Assertions.assertEquals(orderHistory2.getOrder().getId(), result.get(1).getOrderId());
        Assertions.assertEquals(OrderStatus.RETURNING, result.get(1).getStatus());

        verify(orderHistoryRepo, times(1)).findAll();
    }
}