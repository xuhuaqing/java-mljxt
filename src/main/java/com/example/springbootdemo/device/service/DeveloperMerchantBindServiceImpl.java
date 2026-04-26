package com.example.springbootdemo.device.service;

import com.example.springbootdemo.auth.dao.UserMapper;
import com.example.springbootdemo.device.BindDeveloperMerchantRequest;
import com.example.springbootdemo.device.BindDeveloperMerchantVO;
import com.example.springbootdemo.device.DeveloperBoundDeviceVO;
import com.example.springbootdemo.device.DeveloperWithdrawPageVO;
import com.example.springbootdemo.device.DeveloperWithdrawRequest;
import com.example.springbootdemo.device.DeveloperWithdrawVO;
import com.example.springbootdemo.device.dao.DeviceEntity;
import com.example.springbootdemo.device.dao.DeviceMapper;
import com.example.springbootdemo.device.dao.DeveloperMerchantBindMapper;
import com.example.springbootdemo.device.dao.DeveloperMerchantBindRow;
import com.example.springbootdemo.device.dao.DeveloperWithdrawRecordEntity;
import com.example.springbootdemo.device.dao.DeveloperWithdrawRecordMapper;
import com.example.springbootdemo.order.dao.MerchantUsageStatRow;
import com.example.springbootdemo.order.dao.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeveloperMerchantBindServiceImpl implements DeveloperMerchantBindService {

    private final UserMapper userMapper;
    private final DeveloperMerchantBindMapper developerMerchantBindMapper;
    private final DeviceMapper deviceMapper;
    private final OrderMapper orderMapper;
    private final DeveloperWithdrawRecordMapper developerWithdrawRecordMapper;

    public DeveloperMerchantBindServiceImpl(
            UserMapper userMapper,
            DeveloperMerchantBindMapper developerMerchantBindMapper,
            DeviceMapper deviceMapper,
            OrderMapper orderMapper,
            DeveloperWithdrawRecordMapper developerWithdrawRecordMapper
    ) {
        this.userMapper = userMapper;
        this.developerMerchantBindMapper = developerMerchantBindMapper;
        this.deviceMapper = deviceMapper;
        this.orderMapper = orderMapper;
        this.developerWithdrawRecordMapper = developerWithdrawRecordMapper;
    }

    @Override
    @Transactional
    public BindDeveloperMerchantVO bind(BindDeveloperMerchantRequest request) {
        if (userMapper.findDeveloperById(request.getDeveloperId()) == null) {
            throw new IllegalArgumentException("开发不存在");
        }
        if (userMapper.findMerchantById(request.getMerchantId()) == null) {
            throw new IllegalArgumentException("商家不存在");
        }

        int exists = developerMerchantBindMapper.countByDeveloperAndMerchant(
                request.getDeveloperId(), request.getMerchantId()
        );
        if (exists > 0) {
            return new BindDeveloperMerchantVO(request.getDeveloperId(), request.getMerchantId(), true);
        }

        developerMerchantBindMapper.insert(request.getDeveloperId(), request.getMerchantId());
        return new BindDeveloperMerchantVO(request.getDeveloperId(), request.getMerchantId(), false);
    }

    @Override
    public List<DeveloperBoundDeviceVO> listBoundMerchants(Long developerId) {
        if (developerId == null || developerId <= 0) {
            throw new IllegalArgumentException("developerId不能为空且必须大于0");
        }
        if (userMapper.findDeveloperById(developerId) == null) {
            throw new IllegalArgumentException("开发不存在");
        }
        List<DeveloperMerchantBindRow> rows = developerMerchantBindMapper.listByDeveloperId(developerId);
        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> merchantIds = rows.stream().map(DeveloperMerchantBindRow::getMerchantId).distinct().toList();
        Map<Long, Long> merchantTotalUsageMap = new HashMap<>();
        for (MerchantUsageStatRow statRow : orderMapper.statMerchantTotalUsageCount(merchantIds)) {
            merchantTotalUsageMap.put(statRow.getMerchantId(), statRow.getTotalUsageCount());
        }

        return rows.stream()
                .flatMap(r -> {
                    List<DeviceEntity> devices = deviceMapper.findByMerchantId(r.getMerchantId());
                    return devices.stream().map(d -> new DeveloperBoundDeviceVO(
                            r.getBindId(),
                            r.getDeveloperId(),
                            r.getMerchantId(),
                            r.getMerchantName(),
                            r.getMerchantPhone(),
                            r.getRemainingUseCount(),
                            merchantTotalUsageMap.getOrDefault(r.getMerchantId(), 0L),
                            d.getId(),
                            d.getMachineNo(),
                            d.getDeviceName(),
                            d.getStatus(),
                            d.getFreeUseDeadline(),
                            r.getBindTime()
                    ));
                })
                .toList();
    }

    @Override
    @Transactional
    public DeveloperWithdrawVO withdraw(DeveloperWithdrawRequest request) {
        Long developerId = request.getDeveloperId();
        if (userMapper.findDeveloperById(developerId) == null) {
            throw new IllegalArgumentException("开发不存在");
        }
        if (developerWithdrawRecordMapper.countTodayByDeveloperId(developerId) > 0) {
            throw new IllegalArgumentException("今日已提现，请明天再试");
        }

        long totalUsageCount = developerMerchantBindMapper.sumUsageCountByDeveloperId(developerId);
        DeveloperWithdrawRecordEntity entity = new DeveloperWithdrawRecordEntity();
        entity.setDeveloperId(developerId);
        entity.setUsageCountSnapshot(totalUsageCount);
        entity.setCreatedAt(LocalDateTime.now());
        developerWithdrawRecordMapper.insert(entity);

        return new DeveloperWithdrawVO(
                entity.getId(),
                entity.getDeveloperId(),
                entity.getUsageCountSnapshot(),
                entity.getCreatedAt()
        );
    }

    @Override
    public DeveloperWithdrawPageVO listWithdrawRecords(Long developerId, Integer pageNo, Integer pageSize) {
        if (developerId == null || developerId <= 0) {
            throw new IllegalArgumentException("developerId不能为空且必须大于0");
        }
        if (userMapper.findDeveloperById(developerId) == null) {
            throw new IllegalArgumentException("开发不存在");
        }

        int finalPageNo = (pageNo == null || pageNo < 1) ? 1 : pageNo;
        int finalPageSize = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 100);
        int offset = (finalPageNo - 1) * finalPageSize;

        long total = developerWithdrawRecordMapper.countByDeveloperId(developerId);
        List<DeveloperWithdrawVO> records = developerWithdrawRecordMapper.queryByDeveloperId(developerId, offset, finalPageSize)
                .stream()
                .map(r -> new DeveloperWithdrawVO(
                        r.getId(),
                        r.getDeveloperId(),
                        r.getUsageCountSnapshot(),
                        r.getCreatedAt()
                ))
                .toList();

        return new DeveloperWithdrawPageVO(total, finalPageNo, finalPageSize, records);
    }
}
