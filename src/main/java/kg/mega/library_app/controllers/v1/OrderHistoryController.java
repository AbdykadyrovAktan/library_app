package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.mega.library_app.services.OrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order_history")
@Validated
@Tag(name = "Order history controller")
public class OrderHistoryController {
    private final OrderHistoryService orderHistoryService;

    @Autowired
    public OrderHistoryController(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @Operation(summary = "Get complete order history",
            description = """
                    Этот метод возвращает список объектов OrderHistoryResp всех объектов OrderHistory.
                    
                    То есть этот метод показывает всю историю циркуляции книг.
                    
                    This method returns a list of OrderHistoryResp objects of all OrderHistory objects.
                    
                    That is, this method shows the entire circulation history of the books.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrderHistory() {
        return ResponseEntity.ok(orderHistoryService.getAllOrderHistory());
    }

    @Operation(summary = "Get history by order",
            description = """
                    Этот метод возвращает список объектов OrderHistoryResp по ID конкретного объекта Order.
                    
                    This method returns a list of OrderHistoryResp objects by the ID of a particular Order object.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/by_order/{orderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrderHistoryByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderHistoryService.getOrderHistoryByOrderId(orderId));
    }

    @Operation(summary = "Get user's history",
            description = """
                    Этот метод позволяет пользователю смотреть свою историю взятия и возврата книг.
                    
                    This method allows the user to view their borrowing and returning history of books.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getUserHistory() {
        return ResponseEntity.ok(orderHistoryService.getUserHistory());
    }
}
