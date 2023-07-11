package kg.mega.library_app.controllers.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.models.dto.requests.OrderReq;
import kg.mega.library_app.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/orders")
@Validated
@Tag(name = "Order controller")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get order list",
            description = """
                    Этот метод возвращает список объектов OrderResp всех объектов Order в базе данных.
                    
                    This method returns the list of OrderResp objects of all Order objects in the database.
                    """)
    @SecurityRequirement(name = "JWT")
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Check out book",
            description = """
                    Этот метод реализует взятие книги из библиотеки на прочтение.
                    
                    Принимает объект OrderReq.
                    
                    Если количество книг в библиотеке равно нулю, то выбрасывает DataNotFoundException.
                    
                    Автоматически уменьшается количество книг на одну единицу.
                    
                    Также автоматически создаётся запись истории взятия и возврата.
                    
                    This method implements taking a book from the library for reading.
                    
                    It takes an OrderReq object.
                    
                    If the number of books in the library is zero, it throws a DataNotFoundException.
                    
                    The number of books is automatically reduced by one unit.
                    
                    It also automatically creates a record of the take and return history.
                    """)
    @SecurityRequirement(name = "JWT")
    @PostMapping("/take")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> takeBook(@RequestBody OrderReq req) throws DataNotFoundException {
        return ResponseEntity.status(CREATED).body(orderService.takeBook(req));
    }

    @Operation(summary = "Return borrowed book",
            description = """
                    Этот метод реализует возврат книги в библиотеку.
                    
                    Принимает ID объекта Order.
                    
                    Автоматически увеличивая количество книг в библиотеке на одну единицу.
                    
                    Также автоматически создаётся запись истории взятия и возврата.
                    
                    Если эту книгу брал один юзер, но пытается возвратить другой юзер, выбрасывает InadmissibleEditingException.
                    
                    Если по ID объект Order не найден, выбрасывает DataNotFoundException.
                    
                    This method returns the book to the library.
                    
                    It takes the ID of the Order object.
                    
                    Automatically increases the number of books in the library by one.
                    
                    It also automatically creates a record of the pickup and return history.
                    
                    If this book was borrowed by one user but another user tries to return it, it throws InadmissibleEditingException.
                    
                    If Order object is not found by ID, it throws DataNotFoundException.
                    """)
    @SecurityRequirement(name = "JWT")
    @PatchMapping("/return/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> returnBook(@PathVariable Long id) throws InadmissibleEditingException, DataNotFoundException {
        return ResponseEntity.ok(orderService.returnBook(id));
    }
}