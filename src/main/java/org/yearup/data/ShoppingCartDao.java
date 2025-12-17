package org.yearup.data;

import org.yearup.models.ShoppingCart;

import java.util.List;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int user_id);
    ShoppingCart addToCard(int product_id, int user_id);
    void updateShoppingCart(int user_id, int product_id, int quantity);
    void deleteProduct(int user_id);
}
