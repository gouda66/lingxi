scs-web/
├── index.html                    # 入口HTML
├── package.json                  # 项目配置
├── vite.config.ts               # Vite配置（含API代理）
├── tsconfig.json                # TypeScript配置
├── .gitignore
└── src/
├── main.ts                  # Vue应用入口
├── App.vue                  # 根组件
├── api/                     # API模块
│   ├── address.ts           # 地址管理
│   ├── cart.ts              # 购物车
│   ├── category.ts          # 分类管理
│   ├── dish.ts              # 菜品管理
│   ├── employee.ts          # 员工管理
│   ├── order.ts             # 订单管理
│   ├── setmeal.ts           # 套餐管理
│   └── user.ts              # 用户登录
├── router/index.ts          # 路由配置
├── types/index.ts           # TypeScript类型定义
├── utils/
│   ├── request.ts           # Axios封装
│   └── area.ts              # 省市区数据
├── styles/index.scss        # 全局样式
└── views/
├── admin/               # 后台管理端
│   ├── Login.vue        # 管理员登录
│   ├── Layout.vue       # 后台布局
│   ├── category/List.vue
│   ├── dish/List.vue, Add.vue
│   ├── setmeal/List.vue, Add.vue
│   ├── employee/List.vue
│   └── order/List.vue
└── user/                # 用户端
├── Layout.vue       # 用户端布局
├── Login.vue        # 用户登录
├── Home.vue         # 首页（菜品列表）
├── Cart.vue         # 购物车
├── Checkout.vue     # 确认订单
├── Order.vue        # 我的订单
├── Address.vue      # 地址管理
└── UserCenter.vue   # 个人中心