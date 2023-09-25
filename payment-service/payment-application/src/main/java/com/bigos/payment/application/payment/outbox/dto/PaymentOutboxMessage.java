
package com.bigos.payment.application.payment.outbox.dto;

import com.bigos.common.applciaiton.outbox.model.AbstractOutboxMessage;
import com.bigos.common.domain.vo.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOutboxMessage extends AbstractOutboxMessage {

    private PaymentStatus paymentStatus;

}

