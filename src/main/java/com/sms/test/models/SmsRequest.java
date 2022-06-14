package com.sms.test.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SmsRequest {
    private String from;
    private String to;
    private String message;private String transactionId;
}
