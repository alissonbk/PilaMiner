package com.alissonbk.pilacoin.dto;

import lombok.Data;

/**
 * Response generico
 * */
@Data
public class ResponseDTO<T> {
    private T valor;
}
