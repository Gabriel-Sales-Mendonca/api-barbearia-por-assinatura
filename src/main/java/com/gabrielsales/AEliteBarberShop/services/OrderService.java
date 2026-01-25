package com.gabrielsales.AEliteBarberShop.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.gabrielsales.AEliteBarberShop.entities.*;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PlanService planService;
    private final SignatureService signatureService;

    public OrderService(OrderRepository orderRepository, UserService userService, PlanService planService, SignatureService signatureService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.planService = planService;
        this.signatureService = signatureService;
    }

    public Order create(Long planId) {
        User user = this.userService.getTokenUser();
        Plan plan = this.planService.findById(planId);

        Order existingOrder = this.orderRepository.findAllByUserIdAndOrderStatusOrOrderStatus(user.getId(), OrderStatus.AWAITING_PROOF_OF_PAYMENT, OrderStatus.AWAITING_PAYMENT_APPROVAL);
        if (existingOrder != null) {
            log.info("Tentiva de criar pedido de assinatura sendo que já exisitia um pedido com o status aguardando comprovante de pagamento");
            throw new ResourceAlreadyExistsException(
                    "Já existe uma pedido de assinatura com o status: " +
                    OrderStatus.AWAITING_PROOF_OF_PAYMENT.getOrderStatus() +
                    ", cancele seus pedidos de assinatura que estão aguardando comprovante de pagamento para criar uma nova assinatura");
        }

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

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "date")
        );

        User user = this.userService.getTokenUser();
        return this.orderRepository.findAllByUserId(user.getId(), sortedPageable);
    }

    public Order findById(Long id) {
        User user = this.userService.getTokenUser();

        Order order = this.orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        if (order.getUser().getId().equals(user.getId()) ||
                user.getRole().equals(UserRole.ADMIN)) {
            return order;
        } else {
            throw new ResourceNotFoundException(id);
        }
    }

    public Page<Order> findAllToApprove(Pageable pageable) {
        pageable = this.getValidPageable(pageable);

        return this.orderRepository.findAllByOrderStatus(OrderStatus.AWAITING_PAYMENT_APPROVAL, pageable);
    }

    public void receiveProofOfPayment(MultipartFile file) {
        User user = this.userService.getTokenUser();
        Order order = this.orderRepository.findAllByUserIdAndOrderStatus(user.getId(), OrderStatus.AWAITING_PROOF_OF_PAYMENT);

        if (file.isEmpty()) throw new InvalidResourceException("Arquivo enviado está vazio");

        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

        Map params = ObjectUtils.asMap(
                "signature_algorithm", "sha256",
                "type", "authenticated",
                "secure", true);

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            log.info("Upload de arquivo feito com sucesso para o pedido: {}", order.getId());
            String secureUrl = uploadResult.get("secure_url").toString().split("authenticated")[1];

            order.setProofOfPaymentSecureUrl(secureUrl);
            order.setOrderStatus(OrderStatus.AWAITING_PAYMENT_APPROVAL);

            this.orderRepository.save(order);
        } catch (IOException e) {
            log.error("Erro ao fazer o upload do arquivo para o pedido: {}", order.getId(), e);
            throw new RuntimeException("Erro ao fazer o upload do arquivo");
        }
    }

    @Transactional
    public void approveOrRejectPayment(Long orderId, Boolean approve) {
        Order order = this.findById(orderId);
        if (!order.getOrderStatus().equals(OrderStatus.AWAITING_PAYMENT_APPROVAL)) {
            log.warn("Pedido: {} não está na etapa de: {}", orderId, OrderStatus.AWAITING_PAYMENT_APPROVAL.getOrderStatus());
            throw new InvalidResourceException("Pedido não está na etapa de: " + OrderStatus.AWAITING_PAYMENT_APPROVAL.getOrderStatus());
        }

        LocalDate dateNow = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        if (approve.equals(true)) {
            order.setOrderStatus(OrderStatus.PAYMENT_APPROVED);
            order.setDate(dateNow);
            this.orderRepository.save(order);

            this.signatureService.create(dateNow, order.getPlan(), order.getUser());
            log.info("Pagamento aprovado para o pedido: {}", orderId);
        } else {
            order.setOrderStatus(OrderStatus.PAYMENT_REJECTED);
            order.setDate(dateNow);
            this.orderRepository.save(order);
        }
    }

}
