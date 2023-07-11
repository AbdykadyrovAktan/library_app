package kg.mega.library_app.services;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.dao.OrderRepo;
import kg.mega.library_app.models.constants.OrderStatus;
import kg.mega.library_app.models.dto.requests.OrderReq;
import kg.mega.library_app.models.dto.responses.OrderResp;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.Order;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.BookService;
import kg.mega.library_app.services.NotificationService;
import kg.mega.library_app.services.OrderHistoryService;
import kg.mega.library_app.services.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepo orderRepo;

    @Mock
    private OrderHistoryService orderHistoryService;

    @Mock
    private BookService bookService;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(orderRepo, orderHistoryService, bookService, notificationService);

        // ”становка пользовател€ в контекст безопасности
        User user = new User();
        user.setEmail("test@example.com");
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testTakeBook() throws DataNotFoundException {
        OrderReq req = new OrderReq();
        req.setBookId(1L);
        req.setReturnDueDate(LocalDate.now().plusDays(7));

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setQuantity(2);

        User user = new User();
        user.setEmail("test@example.com");

        when(bookService.getById(req.getBookId())).thenReturn(book);
        when(bookService.saveBook(book)).thenReturn(book);
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResp result = orderService.takeBook(req);

        assertEquals(user.getEmail(), result.getUserEmail());
        assertEquals(book.getTitle(), result.getBookTitle());
        assertNotNull(result.getOrderDate());

        assertEquals(1, book.getQuantity());

        verify(bookService, times(1)).getById(req.getBookId());
        verify(bookService, times(1)).saveBook(book);
        verify(orderRepo, times(1)).save(any(Order.class));
        verify(orderHistoryService, times(1)).createOrderHistory(any(Order.class), eq(OrderStatus.TAKING));
        verifyNoMoreInteractions(bookService, orderRepo, orderHistoryService);
    }

    @Test
    void testTakeBookThrowsDataNotFoundException() throws DataNotFoundException {
        OrderReq req = new OrderReq();
        req.setBookId(1L);
        req.setReturnDueDate(LocalDate.now().plusDays(7));

        when(bookService.getById(req.getBookId())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> orderService.takeBook(req));

        verify(bookService, times(1)).getById(req.getBookId());
        verifyNoMoreInteractions(bookService, orderRepo, orderHistoryService);
    }

    @Test
    void testTakeBookThrowsDataNotFoundExceptionWhenBookNotAvailable() throws DataNotFoundException {
        OrderReq req = new OrderReq();
        req.setBookId(1L);
        req.setReturnDueDate(LocalDate.now().plusDays(7));

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Book 1");
        book.setQuantity(0);

        when(bookService.getById(req.getBookId())).thenReturn(book);

        assertThrows(DataNotFoundException.class, () -> orderService.takeBook(req));

        verify(bookService, times(1)).getById(req.getBookId());
        verifyNoMoreInteractions(bookService, orderRepo, orderHistoryService);
    }

    @Test
    void testReturnBookThrowsDataNotFoundException() {
        Long orderId = 1L;

        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.returnBook(orderId));

        verify(orderRepo, times(1)).findById(orderId);
        verifyNoMoreInteractions(bookService, orderRepo, orderHistoryService);
    }

    @Test
    void testGetAllOrders() {
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        Book book1 = new Book();
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setTitle("Book 2");

        Order order1 = new Order();
        order1.setUser(user1);
        order1.setBook(book1);
        order1.setOrderDate(LocalDate.now());

        Order order2 = new Order();
        order2.setUser(user2);
        order2.setBook(book2);
        order2.setOrderDate(LocalDate.now());

        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);

        when(orderRepo.findAll()).thenReturn(orders);

        List<OrderResp> result = orderService.getAllOrders();

        assertEquals(2, result.size());

        verify(orderRepo, times(1)).findAll();
        verifyNoMoreInteractions(bookService, orderRepo, orderHistoryService);
    }
}