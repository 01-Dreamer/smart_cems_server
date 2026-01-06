# Smart Campus Energy Monitoring System (Smart CEMS)

## 1. 项目背景
本项目是一个基于 Spring Boot 和 Vue 的智慧校园能耗监测与管理平台。旨在模拟校园内智能电表的能耗数据采集、存储、分析及异常告警功能。

## 2. 技术栈
- **后端**: Spring Boot 3.2.1, MyBatis Plus, Spring Security, JWT
- **数据库**: MySQL 8.0
- **文档**: Knife4j (Swagger 3)
- **工具**: Lombok, Hutool

## 3. 数据库配置
1. 创建数据库 `energy_20231120171` 
2. 修改 `src/main/resources/application.properties` 中的数据库连接信息。
3. 启动项目，系统会自动初始化所需的表结构和测试数据。

## 4. 启动说明
### 后端
1. 确保 JDK 17+ 和 Maven 已安装。
2. 运行 `mvn clean package` 进行构建。
3. 运行 `java -jar target/smart_cems_server-0.0.1-SNAPSHOT.jar` 启动服务。
4. 访问 `http://localhost:18080/doc.html` 查看 API 文档。

### 前端
(请参考前端项目文档)

## 5. 设计模式应用
本项目显式应用了以下设计模式：
1. **策略模式 (Strategy Pattern)**: 用于处理不同类型的告警逻辑。
   - 位置: `src/main/java/zxylearn/smart_cems_server/strategy/AlertStrategy.java`
2. **观察者模式 (Observer Pattern)**: 用于解耦数据采集与告警处理。
   - 位置: `src/main/java/zxylearn/smart_cems_server/event/AlertEventListener.java`
3. **工厂模式 (Factory Pattern)**: 用于封装模拟数据的生成逻辑。
   - 位置: `src/main/java/zxylearn/smart_cems_server/factory/SimulationDataFactory.java`

## 6. 核心功能
- **数据模拟**: 后端定时任务每 5 秒生成一次能耗数据，模拟真实物理环境波动。
- **异常告警**: 自动检测功率过载或电压异常，并生成告警记录。
- **统计分析**: 提供设备历史趋势和建筑用电占比统计接口。
