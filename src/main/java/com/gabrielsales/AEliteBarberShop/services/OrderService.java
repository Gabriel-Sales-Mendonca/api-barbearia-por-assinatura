package com.gabrielsales.AEliteBarberShop.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gabrielsales.AEliteBarberShop.entities.Order;
import com.gabrielsales.AEliteBarberShop.entities.OrderStatus;
import com.gabrielsales.AEliteBarberShop.entities.Plan;
import com.gabrielsales.AEliteBarberShop.entities.User;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PlanService planService;

    public OrderService(OrderRepository orderRepository, UserService userService, PlanService planService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.planService = planService;
    }

    public Order create(Long planId) {
        User user = this.userService.getTokenUser();
        Plan plan = this.planService.findById(planId);

        Order order = new Order(
                plan.getPrice(),
                LocalDate.now(ZoneId.of("America/Sao_Paulo")),
                OrderStatus.AWAITING_PROOF_OF_PAYMENT,
                user,
                plan
        );

        return this.orderRepository.save(order);
    }

    private Pageable getValidPageable(Pageable pageable) {
        if (pageable.getPageSize() > 50) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    50,
                    pageable.getSort()
            );
        }

        return pageable;
    }

    public Page<Order> findAll(Pageable pageable) {
        pageable = this.getValidPageable(pageable);

        User user = this.userService.getTokenUser();
        return this.orderRepository.findAllByUserId(user.getId(), pageable);
    }

    public Order findById(Long id) {
        User user = this.userService.getTokenUser();

        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (order.getUser().getId().equals(user.getId())) {
            return order;
        } else {
            throw new ResourceNotFoundException(id);
        }
    }

    public Page<Order> findAllToApprove(Pageable pageable) {
        pageable = this.getValidPageable(pageable);

        return this.orderRepository.findAllByOrderStatus(OrderStatus.AWAITING_PAYMENT_APPROVAL, pageable);
    }

    public void receiveProofOfPayment(Long orderId, MultipartFile file) {
        Order order = this.findById(orderId);

        if (file.isEmpty()) throw new InvalidResourceException("Arquivo enviado está vazio");

        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

        Map params = ObjectUtils.asMap(
                "signature_algorithm", "sha256",
                "type", "authenticated",
                "secure", true);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String secureUrl = uploadResult.get("secure_url").toString().split("authenticated")[1];

            order.setProofOfPaymentSecureUrl(secureUrl);
            order.setOrderStatus(OrderStatus.AWAITING_PAYMENT_APPROVAL);

            this.orderRepository.save(order);
        } catch (IOException e) {
            System.out.println("Erro ao fazer o upload do arquivo");
            throw new RuntimeException("Erro ao fazer o upload do arquivo");
        }
    }

}
