package com.bigos.payment.adapters.payment.out.repository;

import com.bigos.common.domain.vo.OrderId;
import com.bigos.payment.adapters.payment.exception.PaymentNotFoundException;
import com.bigos.payment.adapters.payment.model.entity.PaymentEntity;
import com.bigos.payment.adapters.payment.model.mapper.PaymentEntityMapper;
import com.bigos.payment.domain.model.Payment;
import com.bigos.payment.domain.ports.out.repository.PaymentRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentRepositoryJpa paymentRepositoryJpa;
    private final PaymentEntityMapper paymentEntityMapper;

    public PaymentRepositoryImpl(PaymentRepositoryJpa paymentRepositoryJpa, PaymentEntityMapper paymentEntityMapper) {
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
