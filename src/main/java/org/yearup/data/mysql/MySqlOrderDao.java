package org.yearup.data.mysql;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;

public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order CreateUserInfo(Order order, int user_id) {
        String userInfoQuery = """
                INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                Values (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(userInfoQuery, Statement.RETURN_GENERATED_KEYS)){

            preparedStatement.setInt(1, user_id);
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(order.getDate())); // converts Local Date time to sql date time
            preparedStatement.setString(3, order.getAddress());
            preparedStatement.setString(4, order.getCity());
            preparedStatement.setString(5, order.getState());
            preparedStatement.setString(6, order.getZip());
            preparedStatement.setDouble(7, order.getShippingAmount());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()){
                    int resultId = resultSet.getInt(1);
                    order.setOrder_id(resultId);
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
