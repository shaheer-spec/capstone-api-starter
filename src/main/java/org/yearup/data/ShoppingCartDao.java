package org.yearup.data;

import org.yearup.models.ShoppingCart;

import java.util.List;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int user_id);
    ShoppingCart createShoppingCart(ShoppingCart shoppingCart);
    void updateShoppingCart(int user_id, ShoppingCart shoppingCart);
    void deleteShoppingCart(int user_id);
}
