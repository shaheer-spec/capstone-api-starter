package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.OrderLineItem;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public OrderLineItem createOrderLineItem(OrderLineItem orderLineItem) {
        String orderLineQuery = """
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                Values (?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(orderLineQuery, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setInt(1, orderLineItem.getOrderId());
            preparedStatement.setInt(2, orderLineItem.getProductId());
            preparedStatement.setBigDecimal(3, orderLineItem.getSalesPrice());
            preparedStatement.setInt(4, orderLineItem.getQuantity());
            preparedStatement.setBigDecimal(5, orderLineItem.getDiscount());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()){
                    int resultId = resultSet.getInt(1);
                    orderLineItem.setOrderLineIdItem(resultId);
                }else {
                    throw new SQLException("Error in MySqlOrderLineItemDao");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderLineItem;
    }
}
