package ca.gb.comp3095.foodrecipe.model.service;

import java.math.BigDecimal;
import java.util.Map;

public interface ShoppingCartService {

    void addItem(String item);

    void removeItem(String item);

    void deleteItem(String item);

    Map<String, Integer> getItemsInCart();

    void checkout();

    BigDecimal getTotal();
}
