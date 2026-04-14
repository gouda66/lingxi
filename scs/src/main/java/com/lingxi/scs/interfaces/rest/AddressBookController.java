package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.service.AddressBookApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import com.lingxi.scs.domain.model.entity.AddressBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
@RequiredArgsConstructor
public class AddressBookController {

    private final AddressBookApplicationService addressBookService;

    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        log.info("新增地址: {}", addressBook);
        AddressBook saved = addressBookService.addAddress(addressBook, userId);
        return R.success(saved);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        log.info("设置默认地址: {}", addressBook.getId());
        addressBookService.setDefaultAddress(addressBook.getId(), userId);
        return R.success("设置成功");
    }

    /**
     * 修改地址
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        log.info("修改地址: {}", addressBook);
        AddressBook updated = addressBookService.updateAddress(addressBook, userId);
        return R.success(updated);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable String id) {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> addresses = addressBookService.getUserAddresses(userId);
        return addresses.stream()
                .filter(a -> a.getId().equals(Long.parseLong(id)))
                .findFirst()
                .map(R::success)
                .orElse(R.error("地址不存在"));
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook defaultAddress = addressBookService.getDefaultAddress(userId);
        if (defaultAddress == null) {
            return R.error("没有默认地址");
        }
        return R.success(defaultAddress);
    }

    /**
     * 查询用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list() {
        Long userId = BaseContext.getCurrentId();
        log.info("查询用户地址列表: userId={}", userId);
        List<AddressBook> addresses = addressBookService.getUserAddresses(userId);
        return R.success(addresses);
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    public R<String> delete(@RequestParam String id) {
        Long userId = BaseContext.getCurrentId();
        log.info("删除地址: {}", id);
        addressBookService.deleteAddress(Long.parseLong(id), userId);
        return R.success("删除成功");
    }
}
