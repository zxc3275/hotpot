package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.entity.HotpotBase;
import com.hotpot.entity.HotpotType;
import com.hotpot.mapper.HotpotBaseMapper;
import com.hotpot.service.HotpotBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotpotBaseServiceImpl implements HotpotBaseService {
    private final HotpotBaseMapper mapper;

    @Override
    public List<HotpotType> getTypes() { return mapper.findAllTypes(); }
    @Override
    public List<HotpotBase> getBases(Long typeId) {
        if (typeId != null) return mapper.findByType(typeId);
        return mapper.findAll();
    }
    @Override
    public HotpotBase getBaseDetail(Long id) {
        HotpotBase b = mapper.findById(id);
        if (b == null) throw new BusinessException("锅底不存在");
        return b;
    }
    @Override
    public void saveType(HotpotType type) {
        if (type.getId() == null) mapper.insertType(type);
        else mapper.updateType(type);
    }
    @Override
    public void deleteType(Long id) { mapper.deleteType(id); }
    @Override
    public void saveBase(HotpotBase base) {
        if (base.getId() == null) mapper.insert(base);
        else mapper.update(base);
    }
    @Override
    public void deleteBase(Long id) { mapper.delete(id); }
    @Override
    public List<HotpotBase> adminList() { return mapper.findAll(); }
}
