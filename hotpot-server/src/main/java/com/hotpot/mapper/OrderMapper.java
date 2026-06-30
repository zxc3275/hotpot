package com.hotpot.mapper;

import com.hotpot.entity.Order;
import com.hotpot.entity.OrderItem;
import com.hotpot.entity.OrderStatusLog;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order findById(Long id);
    @Select("SELECT * FROM `order` WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(Long userId);
    @Select("SELECT * FROM `order` ORDER BY create_time DESC")
    List<Order> findAll();
    @Select("SELECT * FROM `order` WHERE status = #{status} ORDER BY create_time DESC")
    List<Order> findByStatus(Integer status);
    @Insert("INSERT INTO `order`(order_no,user_id,base_id,base_price,pot_type,spicy_level,num_level,total_price,status,remark,table_no,pay_time,create_time) VALUES(#{orderNo},#{userId},#{baseId},#{basePrice},#{potType},#{spicyLevel},#{numLevel},#{totalPrice},#{status},#{remark},#{tableNo},#{payTime},NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    @Update("UPDATE `order` SET status=#{status},update_time=NOW() WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    @Update("UPDATE `order` SET status=#{status},pay_time=NOW(),update_time=NOW() WHERE id=#{id}")
    int pay(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> findItems(Long orderId);
    @Insert("INSERT INTO order_item(order_id,dish_id,dish_name,dish_image,dish_price,quantity,subtotal,create_time) VALUES(#{orderId},#{dishId},#{dishName},#{dishImage},#{dishPrice},#{quantity},#{subtotal},NOW())")
    int insertItem(OrderItem item);
    @Insert("INSERT INTO order_status_log(order_id,from_status,to_status,remark,operator_id,create_time) VALUES(#{orderId},#{fromStatus},#{toStatus},#{remark},#{operatorId},NOW())")
    int insertStatusLog(OrderStatusLog log);
    @Select("SELECT * FROM order_status_log WHERE order_id = #{orderId} ORDER BY create_time")
    List<OrderStatusLog> findStatusLogs(Long orderId);

    @Select("SELECT IFNULL(SUM(total_price),0) FROM `order` WHERE status IN (2,3,4)")
    Long totalSales();
    @Select("SELECT COUNT(*) FROM `order`")
    Long countAll();
    @Select("SELECT COUNT(*) FROM `order` WHERE status = #{status}")
    Long countByStatus(Integer status);
    @Select("SELECT COUNT(*) FROM `order` WHERE DATE(create_time) = CURDATE()")
    Long countToday();
    @Select("SELECT IFNULL(SUM(total_price),0) FROM `order` WHERE status IN (2,3,4) AND DATE(create_time) = CURDATE()")
    Long todaySales();

    // 近7天每日销售额
    @Select("SELECT DATE(create_time) as date, IFNULL(SUM(total_price),0) as amount FROM `order` WHERE status IN (2,3,4) AND create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> dailySales7Days();

    // 订单状态分布
    @Select("SELECT status, COUNT(*) as count FROM `order` GROUP BY status")
    List<Map<String, Object>> statusDistribution();

    // 热门菜品TOP10
    @Select("SELECT dish_name as name, SUM(quantity) as sales, SUM(subtotal) as amount FROM order_item GROUP BY dish_name ORDER BY sales DESC LIMIT 10")
    List<Map<String, Object>> topDishes();

    // 时段分布
    @Select("SELECT HOUR(create_time) as hour, COUNT(*) as count FROM `order` WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) GROUP BY HOUR(create_time) ORDER BY hour")
    List<Map<String, Object>> hourlyDistribution();
}
