package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.models.dto.requests.OrderReq;
import kg.mega.library_app.models.dto.responses.OrderResp;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    OrderResp takeBook(OrderReq req) throws DataNotFoundException;

    OrderResp returnBook(Long orderId) throws DataNotFoundException;

    List<OrderResp> getAllOrders();
}
