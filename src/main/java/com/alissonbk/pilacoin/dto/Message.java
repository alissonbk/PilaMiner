package com.alissonbk.pilacoin.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class Message {
    private String messageContent;
    private Instant date;
}
