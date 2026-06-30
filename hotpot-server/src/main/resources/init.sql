-- 火锅点餐系统 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS hotpot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hotpot;

-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    phone VARCHAR(20),
    avatar VARCHAR(255),
    gender INT DEFAULT 0,
    email VARCHAR(100),
    balance INT DEFAULT 0 COMMENT '余额(分)',
    points INT DEFAULT 0 COMMENT '积分',
    status INT DEFAULT 1 COMMENT '1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
DROP TABLE IF EXISTS role;
CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜单表
DROP TABLE IF EXISTS menu;
CREATE TABLE menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(255),
    component VARCHAR(255),
    icon VARCHAR(50),
    type INT DEFAULT 1 COMMENT '1菜单 2按钮',
    permission VARCHAR(100),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联表
DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色菜单关联表
DROP TABLE IF EXISTS role_menu;
CREATE TABLE role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜品分类表
DROP TABLE IF EXISTS dish_category;
CREATE TABLE dish_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(255),
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 菜品表
DROP TABLE IF EXISTS dish;
CREATE TABLE dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    price INT DEFAULT 0 COMMENT '价格(分)',
    original_price INT DEFAULT 0 COMMENT '原价(分)',
    spicy_level INT DEFAULT 0 COMMENT '辣度 0-3',
    status INT DEFAULT 1 COMMENT '1上架 0下架',
    sales INT DEFAULT 0 COMMENT '销量',
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 火锅类型表
DROP TABLE IF EXISTS hotpot_type;
CREATE TABLE hotpot_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    image VARCHAR(255),
    status INT DEFAULT 1,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 锅底表
DROP TABLE IF EXISTS hotpot_base;
CREATE TABLE hotpot_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type_id BIGINT,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    price INT DEFAULT 0,
    spicy_min INT DEFAULT 0,
    spicy_max INT DEFAULT 3,
    num_min INT DEFAULT 0 COMMENT '麻度最小值',
    num_max INT DEFAULT 3 COMMENT '麻度最大值',
    pot_type VARCHAR(20) DEFAULT 'single' COMMENT 'single/double/nine',
    status INT DEFAULT 1,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    base_id BIGINT,
    base_price INT DEFAULT 0,
    pot_type VARCHAR(20) DEFAULT 'single',
    spicy_level INT DEFAULT 2,
    num_level INT DEFAULT 2,
    total_price INT DEFAULT 0,
    status INT DEFAULT 0 COMMENT '0待支付 1已支付 2制作中 3已上菜 4已完成 -1已取消',
    remark VARCHAR(500),
    table_no VARCHAR(20),
    pay_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单明细表
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    dish_id BIGINT,
    dish_name VARCHAR(100),
    dish_image VARCHAR(255),
    dish_price INT DEFAULT 0,
    quantity INT DEFAULT 1,
    subtotal INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单状态日志表
DROP TABLE IF EXISTS order_status_log;
CREATE TABLE order_status_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    from_status INT,
    to_status INT,
    remark VARCHAR(255),
    operator_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 购物车表
DROP TABLE IF EXISTS cart_item;
CREATE TABLE cart_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    dish_id BIGINT,
    dish_name VARCHAR(100),
    dish_image VARCHAR(255),
    dish_price INT DEFAULT 0,
    quantity INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分记录表
DROP TABLE IF EXISTS points_record;
CREATE TABLE points_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    points INT DEFAULT 0,
    type INT DEFAULT 1 COMMENT '1获取 2消耗',
    description VARCHAR(255),
    order_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分商品表
DROP TABLE IF EXISTS points_goods;
CREATE TABLE points_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(255),
    description TEXT,
    points INT DEFAULT 0,
    stock INT DEFAULT 0,
    status INT DEFAULT 1,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 兑换记录表
DROP TABLE IF EXISTS exchange_record;
CREATE TABLE exchange_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goods_id BIGINT,
    goods_name VARCHAR(100),
    points INT DEFAULT 0,
    quantity INT DEFAULT 1,
    status INT DEFAULT 0 COMMENT '0待处理 1已处理',
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 充值记录表
DROP TABLE IF EXISTS recharge_record;
CREATE TABLE recharge_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount INT DEFAULT 0,
    pay_method INT DEFAULT 1 COMMENT '1微信 2支付宝',
    status INT DEFAULT 1 COMMENT '1成功 0失败',
    trade_no VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 轮播图表
DROP TABLE IF EXISTS carousel;
CREATE TABLE carousel (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    image_url VARCHAR(255) NOT NULL,
    link_url VARCHAR(255),
    type INT DEFAULT 1 COMMENT '1用户端 2管理端',
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 字典表
DROP TABLE IF EXISTS dict;
CREATE TABLE dict (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    dict_type VARCHAR(50) NOT NULL,
    dict_key VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 意见反馈表
DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT,
    reply TEXT,
    status INT DEFAULT 0 COMMENT '0待回复 1已回复',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    reply_time DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
DROP TABLE IF EXISTS user_favorite;
CREATE TABLE user_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    dish_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 火锅指南表
DROP TABLE IF EXISTS cooking_guide;
CREATE TABLE cooking_guide (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    image VARCHAR(255),
    type VARCHAR(20) DEFAULT 'cooking' COMMENT 'cooking/dipping/blanch',
    status INT DEFAULT 1,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============ 初始数据 ============

-- 管理员账号 admin/123456 (BCrypt)
INSERT INTO user(username,password,nickname,phone,balance,points,status) VALUES
('admin','$2b$10$uRNljxPUlYoAuJN6ZpleW.cfoI1loyrBevBwxQxtmzPcvxzm.EFsi','系统管理员','13800000000',0,0,1),
('testuser','$2b$10$uRNljxPUlYoAuJN6ZpleW.cfoI1loyrBevBwxQxtmzPcvxzm.EFsi','测试用户','13900000000',100000,500,1);

-- 角色
INSERT INTO role(name,code,description) VALUES
('超级管理员','ADMIN','系统超级管理员'),
('普通用户','USER','普通用户');

-- 用户角色关联
INSERT INTO user_role(user_id,role_id) VALUES(1,1),(2,2);

-- 基础菜单
INSERT INTO menu(id,parent_id,name,path,component,icon,type,sort) VALUES
(1,0,'仪表盘','/dashboard','dashboard/index','DataAnalysis',1,1),
(2,0,'菜品管理','/dish','dish/index','Dish',1,2),
(3,0,'订单管理','/order','order/index','Document',1,3),
(4,0,'积分商城','/points','points/index','Present',1,4),
(5,0,'火锅指南','/guide','guide/index','Guide',1,5),
(6,0,'系统管理','/system','system/user','Setting',1,6),
(7,0,'用户管理','/system/user','system/user','User',1,7);

-- 角色菜单关联
INSERT INTO role_menu(role_id,menu_id) VALUES(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7);

-- 菜品分类
INSERT INTO dish_category(name,icon,sort) VALUES
('荤菜','🥩',1),('素菜','🥬',2),('海鲜','🦐',3),('丸滑','🍡',4),('豆制品','🫘',5),('主食','🍚',6),('饮品','🥤',7),('小吃','🍟',8);

-- 火锅类型
INSERT INTO hotpot_type(name,description,image,sort) VALUES
('重庆火锅','麻辣鲜香，正宗重庆味','/uploads/重庆火锅.png',1),
('成都火锅','香辣醇厚，成都风味','/uploads/成都火锅.png',2),
('潮汕牛肉火锅','清汤鲜美，牛肉为主','/uploads/潮汕牛肉火锅.png',3),
('菌菇火锅','菌汤鲜美，健康养生','/uploads/菌汤火锅.png',4);

-- 锅底
INSERT INTO hotpot_base(type_id,name,image,description,price,spicy_min,spicy_max,num_min,num_max,pot_type,sort) VALUES
(1,'经典红油锅底','/uploads/红油锅底.png','正宗重庆牛油锅底，麻辣鲜香',4800,2,3,2,3,'single',1),
(1,'鸳鸯锅底','/uploads/鸳鸯锅.png','一半红油一半清汤',5800,1,3,1,3,'double',2),
(1,'九宫格锅底','/uploads/九宫格重庆火锅.png','经典九宫格，每个格子不同风味',6800,0,3,0,3,'nine',3),
(3,'潮汕清汤锅底','/uploads/潮汕清汤锅底.png','牛骨清汤，原汁原味',3800,0,0,0,0,'single',4),
(4,'菌菇清汤锅底','/uploads/菌汤火锅.png','多种菌菇熬制，鲜美养生',4200,0,0,0,0,'single',5);

-- 示例菜品
INSERT INTO dish(category_id,name,image,description,price,original_price,spicy_level,sales,sort) VALUES
(1,'精品肥牛','/uploads/精品肥牛.jpg','精选进口肥牛，肥瘦相间，涮8秒即可',3800,4800,0,256,1),
(1,'手切鲜牛肉','/uploads/手切鲜牛肉.jpg','现切鲜牛肉，鲜嫩多汁',4200,0,0,189,2),
(1,'秘制嫩牛肉','/uploads/秘制嫩牛肉.jpg','秘制腌制，口感嫩滑',3600,0,1,312,3),
(2,'时蔬拼盘','/uploads/时蔬拼盘.jpg','新鲜时令蔬菜拼盘',1800,2200,0,178,4),
(2,'菌菇拼盘','/uploads/菌菇拼盘.jpg','多种菌菇组合',2600,0,0,145,5),
(3,'鲜虾滑','/uploads/鲜虾滑.jpg','手打虾滑，Q弹鲜美',3200,3800,0,203,6),
(3,'鲜毛肚','/uploads/鲜毛肚.jpg','七上八下，爽脆可口',4800,0,0,267,7),
(4,'撒尿牛丸','/uploads/撒尿牛丸.jpg','经典港式牛丸',2200,0,0,134,8),
(5,'冻豆腐','/uploads/冻豆腐.jpg','吸满汤汁，回味无穷',800,1200,0,98,9),
(6,'手工面','/uploads/手工面.jpg','劲道手工拉面',1200,0,0,87,10);

-- 积分商品
INSERT INTO points_goods(name,image,description,points,stock,sort) VALUES
('火锅底料礼盒','/uploads/火锅底料礼盒.jpg','正宗重庆火锅底料，家庭装',500,100,1),
('定制围裙','/uploads/围裙.jpg','火锅店定制防水围裙',300,200,2),
('50元代金券','/uploads/50元代金券.png','下次消费可直接抵扣',800,50,3),
('精美餐具套装','/uploads/精美餐具套装.jpg','火锅专用餐具4件套',600,80,4);

-- 轮播图
INSERT INTO carousel(title,image_url,link_url,type,sort) VALUES
('新品推荐','/uploads/火锅新品推荐.png','/menu',1,1),
('火锅盛宴','/uploads/火锅盛宴.png','/menu',1,2),
('积分兑换','/uploads/积分兑换.png','/points',1,3);

-- 字典数据
INSERT INTO dict(dict_type,dict_key,dict_value,sort) VALUES
('pay_method','1','微信支付',1),
('pay_method','2','支付宝',2),
('order_status','0','待支付',1),
('order_status','1','已支付',2),
('order_status','2','制作中',3),
('order_status','3','已上菜',4),
('order_status','4','已完成',5),
('order_status','-1','已取消',6);
