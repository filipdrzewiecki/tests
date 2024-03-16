package com.jar.jit.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@Setter
@Entity
public class Account {

    @Id
    private String id;

    private String owner;

    private BigDecimal amount;

    private Currency currency;
}
