package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.entity.*;
import com.hotpot.mapper.PointsMapper;
import com.hotpot.mapper.UserMapper;
import com.hotpot.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {
    private final PointsMapper pointsMapper;
    private final UserMapper userMapper;

    private Long getUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.findByUsername(username).getId();
    }

    @Override
    public List<PointsGoods> getGoods() { return pointsMapper.findGoods(); }

    @Override
    public List<PointsRecord> getRecords() { return pointsMapper.findRecordsByUser(getUserId()); }

    @Override
    public List<ExchangeRecord> getExchanges() { return pointsMapper.findExchangesByUser(getUserId()); }

    @Override
    @Transactional
    public void exchange(Long goodsId, Integer quantity) {
        Long userId = getUserId();
        PointsGoods goods = pointsMapper.findGoodsById(goodsId);
        if (goods == null || goods.getStatus() != 1) throw new BusinessException("商品不存在或已下架");
        if (goods.getStock() < quantity) throw new BusinessException("库存不足");
        int totalPoints = goods.getPoints() * quantity;
        User user = userMapper.findById(userId);
        if (user.getPoints() < totalPoints) throw new BusinessException("积分不足");

        userMapper.deductPoints(userId, totalPoints);
        pointsMapper.deductStock(goodsId, quantity);

        ExchangeRecord record = new ExchangeRecord();
        record.setUserId(userId); record.setGoodsId(goodsId);
        record.setGoodsName(goods.getName()); record.setPoints(totalPoints);
        record.setQuantity(quantity); record.setStatus(0);
        pointsMapper.insertExchange(record);

        PointsRecord pr = new PointsRecord();
        pr.setUserId(userId); pr.setPoints(-totalPoints); pr.setType(2);
        pr.setDescription("兑换商品: " + goods.getName());
        pointsMapper.insertRecord(pr);
    }

    @Override
    public void saveGoods(PointsGoods goods) {
        if (goods.getId() == null) pointsMapper.insertGoods(goods);
        else pointsMapper.updateGoods(goods);
    }

    @Override
    public void deleteGoods(Long id) { pointsMapper.deleteGoods(id); }

    @Override
    public List<ExchangeRecord> allExchanges() { return pointsMapper.findAllExchanges(); }

    @Override
    public void handleExchange(Long id, Integer status, String remark) {
        pointsMapper.updateExchange(id, status, remark);
    }
}
