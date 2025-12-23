package com.gabrielsales.AEliteBarberShop.entities;

public enum OrderStatus {
    AWAITING_PROOF_OF_PAYMENT("Aguardando comprovante de pagamento"),
    AWAITING_PAYMENT_APPROVAL("Aguardando aprovação do pagamento"),
    PAYMENT_APPROVED("Pagamento aprovado"),
    PAYMENT_REJECTED("Pagamento rejeitado");

    private final String orderStatus;

    OrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }
}
