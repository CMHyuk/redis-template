package com.redistemplate.domain.strategy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValueWithTTL<T> {
    T value;
    Long ttl;
}
