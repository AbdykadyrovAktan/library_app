package kg.mega.library_app.services.impl;

import kg.mega.library_app.common.exceptions.DataNotFoundException;
import kg.mega.library_app.common.exceptions.InadmissibleEditingException;
import kg.mega.library_app.dao.OrderRepo;
import kg.mega.library_app.models.dto.requests.OrderReq;
import kg.mega.library_app.models.dto.responses.OrderResp;
import kg.mega.library_app.models.entities.Book;
import kg.mega.library_app.models.entities.Order;
import kg.mega.library_app.models.entities.User;
import kg.mega.library_app.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static kg.mega.library_app.models.constants.OrderStatus.RETURNING;
import static kg.mega.library_app.models.constants.OrderStatus.TAKING;

@Slf4j

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final OrderHistoryService orderHistoryService;
    private final BookService bookService;
    private final NotificationService notificationService;

    @Autowired
    public OrderServiceImpl(OrderRepo orderRepo, OrderHistoryService orderHistoryService, BookService bookService, NotificationService notificationService) {
        this.orderRepo = orderRepo;
        this.orderHistoryService = orderHistoryService;
        this.bookService = bookService;
        this.notificationService = notificationService;
    }

    @Override
    public OrderResp takeBook(OrderReq req) throws DataNotFoundException {
        Book book = bookService.getById(req.getBookId());

        if (book.getQuantity() <= 0) {
            throw new DataNotFoundException("Book is not available!");
        }

        book.setQuantity(book.getQuantity() - 1);
        bookService.saveBook(book);

        Order order = Order
                .builder()
                .user((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .book(book)
                .orderDate(LocalDate.now())
                .returnDueDate(req.getReturnDueDate())
                .actualReturnDate(null)
                .build();
        orderRepo.save(order);
        orderHistoryService.createOrderHistory(order, TAKING);

        log.info("Book '{}' taken by user '{}'", book.getTitle(), order.getUser().getUsername());

        return OrderResp
                .builder()
                .userEmail(order.getUser().getEmail())
                .bookTitle(order.getBook().getTitle())
                .orderDate(order.getOrderDate())
                .build();
    }

    @Override
    public OrderResp returnBook(Long orderId) throws InadmissibleEditingException, DataNotFoundException {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Order order = orderRepo
                .findById(orderId)
                .orElseThrow(() -> new DataNotFoundException("Order with id " + orderId + " not found!"));

        if (!order.getUser().getEmail().equals(user.getEmail())) {
            throw new InadmissibleEditingException("That's not your order!");
        }

        Book book = order.getBook();
        book.setQuantity(book.getQuantity() + 1);
        bookService.saveBook(book);

        order.setActualReturnDate(LocalDate.now());
        orderRepo.save(order);
        orderHistoryService.createOrderHistory(order, RETURNING);

        log.info("Book '{}' returned by user '{}'", book.getTitle(), order.getUser().getUsername());

        return OrderResp
                .builder()
                .userEmail(order.getUser().getEmail())
                .bookTitle(order.getBook().getTitle())
                .orderDate(order.getOrderDate())
                .build();
    }

    @Override
    public List<OrderResp> getAllOrders() {
        return orderRepo.findAll()
                .stream()
                .map(order -> new OrderResp(order.getUser().getEmail(),
                        order.getBook().getTitle(),
                        order.getOrderDate()))
                .collect(Collectors.toList());
    }

    //    @Scheduled(cron = "0 0 0 * * ?") // Каждый день в 00:00
    @Scheduled(cron = "*/10 * * * * *") // Каждые 10 секунд
    public void checkOverdueBooks() {
        List<Order> orders = orderRepo.findAll();
        for (Order order : orders) {
            if (order.getActualReturnDate() == null) {
                long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), order.getReturnDueDate());
                if (daysDifference <= 3) {
                    notificationService.sendOverdueNotification(order.getUser().getEmail(), order.getBook().getTitle());
                    log.info("Sending overdue notification for order: {}", order.getId());
                }
            }
        }
    }
}