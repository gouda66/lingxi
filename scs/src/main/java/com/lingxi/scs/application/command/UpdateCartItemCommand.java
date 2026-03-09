package com.lingxi.scs.application.command;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新购物车项命令
 * 用于修改购物车中商品的数量
 *
 * @author system
 */
@Data
public class UpdateCartItemCommand {

    /**
     * 购物车项ID
     */
    @NotNull(message = "购物车项ID不能为空")
    private Long cartId;

    /**
     * 新数量
     */
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为1")
    private Integer number;
}
