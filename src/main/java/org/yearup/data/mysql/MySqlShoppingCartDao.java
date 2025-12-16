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
                    shoppingCart.add(shoppingCartItem);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shoppingCart;
    }

    @Override
    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart) {
        return null;
    }

    @Override
    public void updateShoppingCart(int user_id, ShoppingCart shoppingCart) {

    }

    @Override
    public void deleteShoppingCart(int user_id) {

    }

//    private ShoppingCartItem mapRow(ResultSet row) throws SQLException
//    {
//
//    }


}
