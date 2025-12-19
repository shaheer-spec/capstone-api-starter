package org.yearup.data.mysql;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order CreateUserInfo(Order order, int user_id) {
        String userInfoQuery = """
                INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                Values (?, now(), ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userInfoQuery, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setInt(1, user_id);
            preparedStatement.setString(2, order.getAddress());
            preparedStatement.setString(3, order.getCity());
            preparedStatement.setString(4, order.getState());
            preparedStatement.setString(5, order.getZip());
            preparedStatement.setDouble(6, order.getShippingAmount());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()){
                    int resultId = resultSet.getInt(1);
                    order.setOrderId(resultId);
                }else {
                    throw new SQLException("Creating Order failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }
}
