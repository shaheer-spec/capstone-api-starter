package org.yearup.data;

import org.yearup.models.OrderLineItem;

public interface OrderLineItemDao {
    void updateItem(int orderLineItemId, OrderLineItem orderLineItem);
}
