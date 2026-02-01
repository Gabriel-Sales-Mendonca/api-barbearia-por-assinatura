package com.gabrielsales.AEliteBarberShop.repositories;

import com.gabrielsales.AEliteBarberShop.entities.Order;
import com.gabrielsales.AEliteBarberShop.entities.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserId(Long id, Pageable pageable);
    Page<Order> findAllByOrderStatus(OrderStatus status, Pageable pageable);
    Order findAllByUserIdAndOrderStatus(Long id, OrderStatus awaitingProofOfPayment);
    Order findAllByUserIdAndOrderStatusIn(Long id, List<OrderStatus> statuses);
}
