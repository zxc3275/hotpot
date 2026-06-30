package com.hotpot.service;

import com.hotpot.entity.PointsRecord;
import com.hotpot.entity.PointsGoods;
import com.hotpot.entity.ExchangeRecord;
import java.util.List;

public interface PointsService {
    List<PointsGoods> getGoods();
    List<PointsRecord> getRecords();
    List<ExchangeRecord> getExchanges();
    void exchange(Long goodsId, Integer quantity);
    // Admin
    void saveGoods(PointsGoods goods);
    void deleteGoods(Long id);
    List<ExchangeRecord> allExchanges();
    void handleExchange(Long id, Integer status, String remark);
}
