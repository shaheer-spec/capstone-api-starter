package org.yearup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yearup.data.*;
import org.yearup.models.*;

@Service
public class CheckoutService {
    private OrderDao orderDao;
    private OrderLineItemDao orderLineItemDao;
    private UserDao userDao;
    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;

    @Autowired
    public CheckoutService(OrderDao orderDao, OrderLineItemDao orderLineItemDao, UserDao userDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao) {
        this.orderDao = orderDao;
        this.orderLineItemDao = orderLineItemDao;
        this.userDao = userDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }

    public Order checkout (String username){
        User user = userDao.getByUserName(username);
        int userId = user.getId();
        Profile profile = profileDao.getProfile(userId);
        ShoppingCart shoppingCartDao1 = shoppingCartDao.getByUserId(userId);

        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(0);

        Order newOrder = orderDao.CreateUserInfo(order, userId);

        for (ShoppingCartItem shoppingCartItem : shoppingCartDao1.getItems().values()){
            OrderLineItem orderLineItem = new OrderLineItem(
                    newOrder.getOrderId(),
                    shoppingCartItem.getProductId(),
                    shoppingCartItem.getProduct().getPrice(),
                    shoppingCartItem.getQuantity(),
                    shoppingCartItem.getDiscountPercent()
                    );
            orderLineItemDao.createOrderLineItem(orderLineItem);
        }

        shoppingCartDao.deleteProduct(userId);
        return newOrder;
    }
}
