package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        String shoppingCartQuery = """
                SELECT *
                        FROM shopping_cart s
                        INNER JOIN products p
                        ON s.product_id = p.product_id
                        WHERE s.user_id = ?
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(shoppingCartQuery)){

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    Product product = new Product(
                            resultSet.getInt("product_id"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("category_id"),
                            resultSet.getString("description"),
                            resultSet.getString("subcategory"),
                            resultSet.getInt("stock"),
                            resultSet.getBoolean("featured"),
                            resultSet.getString("image_url")
                    );

                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setProduct(product);
                    shoppingCartItem.setQuantity(resultSet.getInt("quantity"));
                    shoppingCartItem.setDiscountPercent(BigDecimal.ZERO);
                    shoppingCart.add(shoppingCartItem);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shoppingCart;
    }

    @Override
    public ShoppingCart addToCard(int product_id, int user_id) {
        String addingToCartQuery = """
                INSERT INTO shopping_cart (user_id, product_id, quantity)
                        VALUES (?, ?, 1)
                """;

        String updatingCartQuery = """
                UPDATE shopping_cart
                SET quantity = quantity + 1
                WHERE user_id = ? AND product_id = ?;
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updatingCartQuery)){

            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, product_id);

            int updatedCart = preparedStatement.executeUpdate();

            if (updatedCart == 0){
                try(PreparedStatement insertStatement = connection.prepareStatement(addingToCartQuery)){
                    insertStatement.setInt(1, user_id);
                    insertStatement.setInt(2, product_id);
                    insertStatement.executeUpdate();
                }
            }

        }  catch (SQLException e) {
            e.printStackTrace();
        }
        return getByUserId(user_id);
    }

    @Override
    public void updateShoppingCart(int user_id, ShoppingCart shoppingCart) {

    }

    @Override
    public void deleteProduct(int user_id) {

    }
}
