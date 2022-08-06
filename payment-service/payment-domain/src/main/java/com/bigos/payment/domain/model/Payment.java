package com.bigos.payment.domain.model;

import com.bigos.common.domain.model.AggregateRoot;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.domain.exception.PaymentDomainException;
import com.bigos.payment.domain.model.vo.PaymentId;
import com.bigos.payment.domain.model.vo.PaymentStatus;
import com.bigos.payment.domain.model.vo.WalletId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static com.bigos.payment.domain.model.vo.PaymentStatus.*;

@Builder
@Getter
public class Payment implements AggregateRoot {

    private PaymentId id;

    private OrderId orderId;

    private WalletId walletId;

    private Money price;

    private PaymentStatus status;

    private Instant createdDate;

    public void initialize() {
        id = new PaymentId(UUID.randomUUID());
        createdDate = Instant.now();
    }

    public void validatePaymentPrice() {
        if (!price.isGreaterThanZero()) {
            throw new PaymentDomainException("Payment price must be greater than zero. Payment price: " + price.amount());
        }
    }

    public void complete() {
        status = COMPLETED;
    }

    public void cancel() {
        status = CANCELLED;
    }

    public void rejected() {
        status = REJECTED;
    }

    public boolean isCompleted() {
        return status.equals(COMPLETED);
    }

    public boolean isRejected() {
        return status.equals(REJECTED);
    }

    public boolean isCanceled() {
        return status.equals(CANCELLED);
    }
}
