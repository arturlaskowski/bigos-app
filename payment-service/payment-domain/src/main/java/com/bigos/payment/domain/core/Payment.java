package com.bigos.payment.domain.core;

import com.bigos.common.domain.model.AggregateRoot;
import com.bigos.common.domain.vo.CustomerId;
import com.bigos.common.domain.vo.Money;
import com.bigos.common.domain.vo.OrderId;
import com.bigos.common.domain.vo.PaymentStatus;
import com.bigos.payment.domain.exception.PaymentDomainException;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

import static com.bigos.common.domain.vo.PaymentStatus.*;

@Builder
@Getter
public class Payment implements AggregateRoot {

    private PaymentId id;

    private OrderId orderId;

    private CustomerId customerId;

    private Money price;

    private PaymentStatus status;

    private Instant creationDate;

    public void initialize() {
        id = new PaymentId(UUID.randomUUID());
        creationDate = Instant.now();
    }

    public void validatePaymentPrice() {
        if (!price.isGreaterThanZero()) {
            throw new PaymentDomainException("Payment price must be greater than zero. Payment price: " + price.amount());
        }
    }

    public void complete() {
        if (COMPLETED == status || CANCELLED == status) {
            throw new PaymentDomainException("The complete operation cannot be performed. Payment is in incorrect state: " + status);
        }
        status = COMPLETED;
    }

    public void cancel() {
        if (CANCELLED == status) {
            throw new PaymentDomainException("The cancel operation cannot be performed. Payment is in incorrect state: " + status);
        }
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
