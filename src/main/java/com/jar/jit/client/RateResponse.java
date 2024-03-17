package com.jar.jit.client;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Data
public class RateResponse {
    private Currency code;
    private List<Rate> rates;

    @Data
    public static class Rate {
        private BigDecimal mid;
    }
}
