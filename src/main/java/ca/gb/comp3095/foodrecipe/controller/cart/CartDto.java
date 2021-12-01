package ca.gb.comp3095.foodrecipe.controller.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class CartDto {
    Instant createdAt;
    Long userId;
    List<String> items;
}
