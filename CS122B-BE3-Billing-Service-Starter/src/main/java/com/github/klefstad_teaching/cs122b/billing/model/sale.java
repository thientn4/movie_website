package com.github.klefstad_teaching.cs122b.billing.model;

import java.math.BigDecimal;
import java.time.Instant;

public class sale {
    public Long getSaleId() {
        return saleId;
    }

    public sale setSaleId(Long saleId) {
        this.saleId = saleId;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public sale setTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public sale setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    private Long saleId;
    private BigDecimal total;
    private Instant orderDate;
}
