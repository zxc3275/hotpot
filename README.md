# 🍲 火锅点餐系统 (Hotpot Ordering System)

在线火锅点餐系统，包含用户端、管理后台和 Java 后端。

## ✨ 功能特性

### 🧑 用户端 (Client)
- 🔥 火锅主页 — 轮播图、锅底/菜品浏览、火锅指南
- 🛒 购物车 — 添加/修改菜品、选择辣度麻度
- 📦 在线下单 — 选择锅底、提交订单
- 📋 订单追踪 — 实时查看订单状态（制作中→上菜→完成）
- 🎁 积分商城 — 积分兑换礼品/代金券
- 👤 个人中心 — 余额充值、个人信息管理

### 🛠 管理后台 (Admin)
- 📊 数据仪表盘 — 销售趋势、订单分布、热销排行
- 🍖 菜品管理 — 分类、菜品 CRUD、上下架
- 📝 订单管理 — 订单列表、状态流转
- 🎁 积分商城管理 — 商品管理、兑换处理
- 📖 火锅指南 — 涮煮/蘸料/焯烫指南编辑
- ⚙️ 系统管理 — 用户管理、角色权限、字典配置

## 🏗 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.5 + Spring Security |
| ORM | MyBatis + Druid 连接池 |
| 认证 | JWT (jjwt 0.12) |
| 数据库 | MySQL 8.0 |
| 前端框架 | Vue 3 (Composition API) |
| 状态管理 | Pinia |
| UI 组件库 | Element Plus |
| 图表 | ECharts 5 |
| 构建工具 | Vite 5 (前端) + Maven (后端) |

## 📂 项目结构

```
火锅/
├── hotpot-server/          # Spring Boot 后端 (Java 17)
│   ├── src/main/java/com/hotpot/
│   │   ├── config/         # Security、CORS 配置
│   │   ├── controller/     # admin/ client/ common/ 控制器
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # MyBatis Mapper 接口
│   │   ├── entity/         # 数据实体
│   │   ├── dto/            # 数据传输对象
│   │   ├── common/         # Result、异常处理
│   │   └── utils/          # JWT 工具类
│   ├── src/main/resources/
│   │   ├── init.sql        # 建表 + 初始数据
│   │   └── mapper/         # MyBatis XML
│   └── pom.xml
├── hotpot-admin/           # Vue 3 管理前端 (端口 5173)
│   └── src/views/          # 登录/仪表盘/菜品/订单/积分/指南/系统
├── hotpot-client/          # Vue 3 用户前端 (端口 5174)
│   └── src/views/          # 首页/菜单/购物车/订单/积分/登录
├── .gitignore
└── README.md
```

## 🚀 快速开始

### 环境要求

- **JDK 17+**
- **Maven 3.9+**
- **MySQL 8.0+**（运行中，端口 3306）
- **Node.js 18+**（推荐 20+）

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd 火锅
```

### 2. 初始化数据库

用 MySQL 客户端执行初始化脚本：

```bash
mysql -u root -p < hotpot-server/src/main/resources/init.sql
```

### 3. 配置后端

```bash
cd hotpot-server

# 复制配置模板并修改数据库密码
cp src/main/resources/application-example.yml src/main/resources/application.yml
# 编辑 application.yml，修改 password 和 jwt.secret
```

### 4. 启动后端

```bash
cd hotpot-server
./mvnw spring-boot:run
# 后端运行在 http://localhost:8080
# Druid 监控：http://localhost:8080/druid/
```

### 5. 启动前端

```bash
# 终端 2 — 管理端
cd hotpot-admin
npm install
npm run dev
# 管理端运行在 http://localhost:5173

# 终端 3 — 用户端
cd hotpot-client
npm install
npm run dev
# 用户端运行在 http://localhost:5174
```

### 6. 访问系统

| 入口 | 地址 | 默认账号 |
|------|------|----------|
| 管理后台 | http://localhost:5173 | `admin` / `123456` |
| 用户端 | http://localhost:5174 | `testuser` / `123456` |

## 🔌 API 概览

| 前缀 | 说明 | 权限 |
|------|------|------|
| `/api/auth/**` | 登录/注册/用户信息 | 公开 |
| `/api/admin/**` | 管理端接口 | ADMIN 角色 |
| `/api/client/**` | 用户端接口 | USER/ADMIN 角色 |
| `/api/common/**` | 公共接口（文件上传等） | 公开 |

## 📄 License

MIT License
