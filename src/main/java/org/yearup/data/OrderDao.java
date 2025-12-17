package org.yearup.data;

import org.yearup.models.Order;

public interface OrderDao {
    Order CreateUserInfo(Order order, int user_id);
}
