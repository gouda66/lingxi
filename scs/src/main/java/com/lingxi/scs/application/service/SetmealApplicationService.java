package com.lingxi.scs.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.lingxi.scs.application.command.SetmealDishCommand;
import com.lingxi.scs.application.dto.SetmealDTO;
import com.lingxi.scs.common.exception.CustomException;
import com.lingxi.scs.domain.model.entity.Setmeal;
import com.lingxi.scs.domain.model.entity.SetmealDish;
import com.lingxi.scs.domain.repository.SetmealDishRepository;
import com.lingxi.scs.domain.repository.SetmealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐应用服务
 *
 * @author system
 */
@Service
@RequiredArgsConstructor
public class SetmealApplicationService {

    private final SetmealRepository setmealRepository;
    private final SetmealDishRepository setmealDishRepository;
    
    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 新增套餐（包含套餐菜品关系）
     *
     * @param setmeal 套餐信息
     * @param setmealDishes 套餐菜品列表
     * @param operatorId 操作人ID
     * @return 保存后的套餐
     */
    @Transactional
    public Setmeal addSetmealWithDishes(Setmeal setmeal, List<SetmealDish> setmealDishes, Long operatorId) {
        setmeal.setStatus(1);
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateUser(operatorId);
        setmeal.setUpdateUser(operatorId);

        Setmeal savedSetmeal = setmealRepository.save(setmeal);

        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish dish : setmealDishes) {
                dish.setSetmealId(savedSetmeal.getId());
            }
            setmealDishRepository.saveAll(setmealDishes);
        }

        return savedSetmeal;
    }

    @Transactional
    public Setmeal addSetmealWithDishes(com.lingxi.scs.application.dto.SetmealDTO setmealDTO, 
                                        List<com.lingxi.scs.application.command.SetmealDishCommand> setmealDishCommands, 
                                        Long operatorId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setId(snowflakeGenerator.next());
        setmeal.setCategoryId(setmealDTO.getCategoryId());
        setmeal.setName(setmealDTO.getName());
        setmeal.setPrice(setmealDTO.getPrice());
        setmeal.setCode(setmealDTO.getCode());
        setmeal.setDescription(setmealDTO.getDescription());
        setmeal.setImage(setmealDTO.getImage());
        
        setmeal.setStatus(1);
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateUser(operatorId);
        setmeal.setUpdateUser(operatorId);

        Setmeal savedSetmeal = setmealRepository.save(setmeal);

        if (setmealDishCommands != null && !setmealDishCommands.isEmpty()) {
            cn.hutool.core.lang.generator.SnowflakeGenerator snowflakeGenerator = new cn.hutool.core.lang.generator.SnowflakeGenerator();
            List<SetmealDish> setmealDishes = setmealDishCommands.stream()
                    .map(cmd -> {
                        SetmealDish dish = new SetmealDish();
                        dish.setId(snowflakeGenerator.next());
                        dish.setDishId(cmd.getDishId());
                        dish.setName(cmd.getName());
                        dish.setPrice(cmd.getPrice());
                        dish.setCopies(cmd.getCopies());
                        dish.setSort(0);
                        dish.setSetmealId(savedSetmeal.getId());
                        dish.setCreateTime(LocalDateTime.now());
                        dish.setUpdateTime(LocalDateTime.now());
                        dish.setCreateUser(operatorId);
                        dish.setUpdateUser(operatorId);
                        dish.setIsDeleted(false);
                        return dish;
                    })
                    .collect(Collectors.toList());
            setmealDishRepository.saveAll(setmealDishes);
        }

        return savedSetmeal;
    }

    /**
     * 更新套餐（包含套餐菜品关系）
     *
     * @param setmeal 套餐信息
     * @param setmealDishes 套餐菜品列表
     * @param operatorId 操作人ID
     * @return 更新后的套餐
     */
    @Transactional
    public Setmeal updateSetmealWithDishes(Setmeal setmeal, List<SetmealDish> setmealDishes, Long operatorId) {
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(operatorId);

        Setmeal savedSetmeal = setmealRepository.save(setmeal);

        setmealDishRepository.deleteBySetmealId(setmeal.getId());

        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish dish : setmealDishes) {
                dish.setSetmealId(savedSetmeal.getId());
            }
            setmealDishRepository.saveAll(setmealDishes);
        }

        return savedSetmeal;
    }

    @Transactional
    public Setmeal updateSetmealWithDishes(SetmealDTO setmealDTO,
                                           List<com.lingxi.scs.application.command.SetmealDishCommand> setmealDishCommands, 
                                           Long operatorId) {
        Setmeal setmeal = getSetmealById(setmealDTO.getId());
        setmeal.setCategoryId(setmealDTO.getCategoryId());
        setmeal.setName(setmealDTO.getName());
        setmeal.setPrice(setmealDTO.getPrice());
        setmeal.setCode(setmealDTO.getCode());
        setmeal.setDescription(setmealDTO.getDescription());
        setmeal.setImage(setmealDTO.getImage());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(operatorId);

        Setmeal savedSetmeal = setmealRepository.save(setmeal);

        setmealDishRepository.deleteBySetmealId(setmeal.getId());

        if (setmealDishCommands != null && !setmealDishCommands.isEmpty()) {
            cn.hutool.core.lang.generator.SnowflakeGenerator snowflakeGenerator = new cn.hutool.core.lang.generator.SnowflakeGenerator();
            List<SetmealDish> setmealDishes = setmealDishCommands.stream()
                    .map(cmd -> {
                        SetmealDish dish = new SetmealDish();
                        dish.setId(setmeal.getId());
                        dish.setDishId(cmd.getDishId());
                        dish.setName(cmd.getName());
                        dish.setPrice(cmd.getPrice());
                        dish.setCopies(cmd.getCopies());
                        dish.setSort(0);
                        dish.setSetmealId(savedSetmeal.getId());
                        dish.setCreateTime(LocalDateTime.now());
                        dish.setUpdateTime(LocalDateTime.now());
                        dish.setCreateUser(operatorId);
                        dish.setUpdateUser(operatorId);
                        dish.setIsDeleted(false);
                        return dish;
                    })
                    .collect(Collectors.toList());
            setmealDishRepository.saveAll(setmealDishes);
        }

        return savedSetmeal;
    }

    /**
     * 根据ID查询套餐
     *
     * @param id 套餐ID
     * @return 套餐信息
     */
    public Setmeal getSetmealById(Long id) {
        return setmealRepository.findById(id)
                .orElseThrow(() -> new CustomException("套餐不存在"));
    }

    /**
     * 根据分类ID查询套餐
     *
     * @param categoryId 分类ID
     * @return 套餐列表
     */
    public List<Setmeal> getSetmealsByCategoryId(Long categoryId) {
        return setmealRepository.findByCategoryId(categoryId);
    }

    /**
     * 查询所有套餐
     *
     * @return 套餐列表
     */
    public List<Setmeal> getAllSetmeals() {
        return setmealRepository.findAll();
    }

    /**
     * 查询套餐的菜品列表
     *
     * @param setmealId 套餐ID
     * @return 套餐菜品列表
     */
    public List<SetmealDish> getSetmealDishes(Long setmealId) {
        return setmealDishRepository.findBySetmealId(setmealId);
    }

    /**
     * 启用/禁用套餐
     *
     * @param id 套餐ID
     * @param status 状态
     * @param operatorId 操作人ID
     */
    @Transactional
    public void updateStatus(Long id, Integer status, Long operatorId) {
        Setmeal setmeal = setmealRepository.findById(id)
                .orElseThrow(() -> new CustomException("套餐不存在"));
        setmeal.setStatus(status);
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setUpdateUser(operatorId);
        setmealRepository.save(setmeal);
    }

    @Transactional
    public void updateStatus(List<Long> ids, Integer status, Long operatorId) {
        for (Long id : ids) {
            Setmeal setmeal = setmealRepository.findById(id)
                    .orElseThrow(() -> new CustomException("套餐不存在"));
            setmeal.setStatus(status);
            setmeal.setUpdateTime(LocalDateTime.now());
            setmeal.setUpdateUser(operatorId);
            setmealRepository.save(setmeal);
        }
    }

    /**
     * 删除套餐
     *
     * @param id 套餐 ID
     */
    @Transactional
    public void deleteSetmeal(Long id) {
        Setmeal setmeal = setmealRepository.findById(id)
                .orElseThrow(() -> new CustomException("套餐不存在"));

        if (setmeal.getStatus() == 1) {
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        // 删除套餐菜品关系
        setmealDishRepository.deleteBySetmealId(id);
        // 删除套餐
        setmealRepository.deleteById(id);
    }

    /**
     * 根据名称模糊查询套餐（分页）
     *
     * @param page 页码（从 1 开始）
     * @param pageSize 每页大小
     * @param name 套餐名称（支持模糊匹配）
     * @return 分页查询结果
     */
    public Page<Setmeal> pageByName(int page, int pageSize, String name) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return setmealRepository.findByNameContaining(name, pageable);
    }
}
