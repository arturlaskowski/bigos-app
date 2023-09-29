package com.bigos.payment.adapters.payment.repository;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.application.payment.exception.PaymentNotFoundException;
import com.bigos.payment.domain.core.Payment;
import com.bigos.payment.domain.port.PaymentRepository;
import com.bigos.payment.entities.PaymentEntity;
import com.bigos.payment.entities.PaymentEntityMapper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class SqlPaymentRepository implements PaymentRepository {

    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final PaymentEntityMapper paymentEntityMapper;

    public SqlPaymentRepository(PaymentRepositoryJpa paymentRepositoryJpa, PaymentEntityMapper paymentEntityMapper) {
        this.paymentRepositoryJpa = paymentRepositoryJpa;
        this.paymentEntityMapper = paymentEntityMapper;
    }

    @Override
    public Payment save(Payment payment) {
        return paymentEntityMapper.paymentEntityToPayment(
                paymentRepositoryJpa.save(paymentEntityMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Payment getByOrderId(OrderId orderId) {
        return paymentRepositoryJpa.findByOrderId(orderId.id())
                .map(paymentEntityMapper::paymentEntityToPayment)
                .orElseThrow(() -> new PaymentNotFoundException("Could not find payment with orderId: " + orderId.id()));
    }
}

@Repository
interface PaymentRepositoryJpa extends CrudRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderId(UUID orderID);
}
