package com.lingxi.scs.interfaces.rest;

import com.lingxi.scs.application.command.AddToCartCommand;
import com.lingxi.scs.application.dto.ShoppingCartDTO;
import com.lingxi.scs.application.service.ShoppingCartApplicationService;
import com.lingxi.scs.common.context.BaseContext;
import com.lingxi.scs.common.result.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartApplicationService shoppingCartService;

    /**
     * 添加购物车
     */
    @PostMapping("/add")
    public R<ShoppingCartDTO> add(@RequestBody AddToCartCommand command) {
        log.info("添加购物车: {}", command);
        Long userId = BaseContext.getCurrentId();
        ShoppingCartDTO cart = shoppingCartService.addToCart(command, userId);
        return R.success(cart);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCartDTO>> list() {
        log.info("查看购物车...");
        Long userId = BaseContext.getCurrentId();
        List<ShoppingCartDTO> cartList = shoppingCartService.getCartList(userId);
        return R.success(cartList);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        log.info("清空购物车");
        Long userId = BaseContext.getCurrentId();
        shoppingCartService.clearCart(userId);
        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车商品数量
     */
    @PostMapping("/sub")
    public R<ShoppingCartDTO> sub(@RequestParam String cartId) {
        log.info("减少购物车商品: {}", cartId);
        Long userId = BaseContext.getCurrentId();
        ShoppingCartDTO cart = shoppingCartService.decreaseCartItem(Long.parseLong(cartId), userId);
        return R.success(cart);
    }
}