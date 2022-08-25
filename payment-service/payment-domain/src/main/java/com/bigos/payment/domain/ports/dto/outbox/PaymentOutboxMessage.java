
package com.bigos.payment.domain.ports.dto.outbox;

import com.bigos.common.domain.outbox.AbstractOutboxMessage;
import com.bigos.common.domain.vo.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOutboxMessage extends AbstractOutboxMessage {

    private PaymentStatus paymentStatus;

}

