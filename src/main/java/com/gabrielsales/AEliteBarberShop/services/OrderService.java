package com.gabrielsales.AEliteBarberShop.services;

import com.gabrielsales.AEliteBarberShop.dtos.CloudinaryResponseDTO;
import com.gabrielsales.AEliteBarberShop.entities.*;
import com.gabrielsales.AEliteBarberShop.repositories.OrderRepository;
import com.gabrielsales.AEliteBarberShop.services.exceptions.InvalidResourceException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceAlreadyExistsException;
import com.gabrielsales.AEliteBarberShop.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final PlanService planService;
    private final SignatureService signatureService;
    private final RestClient cloudinaryClient;

    public OrderService(OrderRepository orderRepository,
                        UserService userService,
                        PlanService planService,
                        SignatureService signatureService,
                        @Qualifier("cloudinaryClient") RestClient cloudinaryClient) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.planService = planService;
        this.signatureService = signatureService;
        this.cloudinaryClient = cloudinaryClient;
    }

    public Order create(Long planId) {
        User user = this.userService.getTokenUser();
        Plan plan = this.planService.findById(planId);

        Order existingOrder = this.orderRepository.findAllByUserIdAndOrderStatusIn(user.getId(), Arrays.asList(OrderStatus.AWAITING_PROOF_OF_PAYMENT, OrderStatus.AWAITING_PAYMENT_APPROVAL));
        if (existingOrder != null) {
            if (existingOrder.getOrderStatus().equals(OrderStatus.AWAITING_PROOF_OF_PAYMENT)) {
                log.info("Tentiva de criar pedido de assinatura sendo que já exisitia um pedido com o status {}", OrderStatus.AWAITING_PROOF_OF_PAYMENT);
                throw new ResourceAlreadyExistsException(
                        "Já existe uma pedido de assinatura com o status: " +
                                existingOrder.getOrderStatus().getOrderStatus().toUpperCase() +
                                ". Envie um comprovante de pagamento ou cancele esse pedido para criar uma nova assinatura.");
            }
            if (existingOrder.getOrderStatus().equals(OrderStatus.AWAITING_PAYMENT_APPROVAL)) {
                log.info("Tentiva de criar pedido de assinatura sendo que já exisitia um pedido com o status {}", OrderStatus.AWAITING_PAYMENT_APPROVAL);
                throw new ResourceAlreadyExistsException(
                        "Já existe uma pedido de assinatura com o status: " +
                                existingOrder.getOrderStatus().getOrderStatus().toUpperCase() +
                                ". Aguarde a aprovação do pagamento ou se você tiver enviado um comprovante errado ou escolhido um plano errado, cancele o pedido, crie um novo e envie o comprovante.");
            }
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

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            body.add("type", "authenticated");
            body.add("signature_algorithm", "sha256");

            CloudinaryResponseDTO response = cloudinaryClient.post()
                    .uri("/image/upload")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(CloudinaryResponseDTO.class);

            log.info("Upload de arquivo feito com sucesso para o pedido: {}", order.getId());
            String fullUrl = response.secureUrl();
            String secureUrl = fullUrl.split("authenticated")[1];

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

    public Order cancel() {
        User user = this.userService.getTokenUser();
        log.info("Cancelando pedido do usuário: {}", user.getId());

        Order existingOrder = this.orderRepository.findAllByUserIdAndOrderStatusIn(user.getId(), Arrays.asList(OrderStatus.AWAITING_PROOF_OF_PAYMENT, OrderStatus.AWAITING_PAYMENT_APPROVAL));
        if (existingOrder == null) {
            log.warn("Falha ao cancelar, não existe nenhum pedido aguardando comprovante ou aprovação de pagamento.");
            throw new InvalidResourceException("Não existe nenhum pedido aguardando comprovante ou aprovação de pagamento.");
        }

        existingOrder.setOrderStatus(OrderStatus.CANCELED_ORDER);

        return this.orderRepository.save(existingOrder);
    }

}
