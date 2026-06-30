package com.hotpot.service;

import com.hotpot.entity.HotpotBase;
import com.hotpot.entity.HotpotType;
import java.util.List;

public interface HotpotBaseService {
    List<HotpotType> getTypes();
    List<HotpotBase> getBases(Long typeId);
    HotpotBase getBaseDetail(Long id);
    // Admin
    void saveType(HotpotType type);
    void deleteType(Long id);
    void saveBase(HotpotBase base);
    void deleteBase(Long id);
    List<HotpotBase> adminList();
}
