package ca.gb.comp3095.foodrecipe.model.service;

import ca.gb.comp3095.foodrecipe.model.repo.RecipeRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    RecipeRespository recipeRespository;

    Map<String, Integer> items = new HashMap<>();

    @Override
    public void addItem(String item) {
        if (items.computeIfPresent(item, (k, v) -> v + 1) == null) {
            items.put(item, 1);
        }
    }

    @Override
    public void deleteItem(String item) {
        items.computeIfPresent(item, (k, v) -> null);
    }

    @Override
    public void removeItem(String item) {
        items.computeIfPresent(item, (k, v) -> {
            if (v > 1) {
                return v - 1;
            } else {
                return null;
            }
        });

    }

    @Override
    public Map<String, Integer> getItemsInCart() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    public void checkout() {

    }

    @Override
    public BigDecimal getTotal() {
        return items.entrySet().stream()
                .map(e -> BigDecimal.valueOf(e.getValue()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
