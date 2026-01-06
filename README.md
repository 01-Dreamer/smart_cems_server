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
本项目显式应用了以下设计模式，以提高代码的可维护性和扩展性：

1. **策略模式 (Strategy Pattern)**
   - **用途**: 定义一系列告警检查算法（如过载检测、电压异常检测），并将它们封装起来，使它们可以相互替换。
   - **实现**: `AlertStrategy` 接口定义了统一的 `check` 方法。`AlertEventListener` 遍历所有实现了该接口的 Bean 来对同一份数据执行多种检查，无需修改主流程即可新增告警规则。
   - **核心位置**: `src/main/java/zxylearn/smart_cems_server/strategy/AlertStrategy.java`

2. **观察者模式 (Observer Pattern) - 基于 RabbitMQ**
   - **用途**: 实现数据采集模块（被观察者）与告警分析模块（观察者）的完全解耦和异步通信。
   - **实现**: `SimulationTask` 作为生产者，将采集到的能耗数据封装为消息发布到 RabbitMQ 交换机。`AlertEventListener` 作为消费者监听队列，一旦收到消息自动触发告警分析逻辑。这种方式避免了采集任务阻塞，并支持流量削峰。
   - **核心位置**: `src/main/java/zxylearn/smart_cems_server/event/AlertEventListener.java`

3. **工厂模式 (Factory Pattern)**
   - **用途**: 封装复杂的对象创建逻辑，特别是带有随机性和业务规则（如模拟故障注入）的数据生成过程。
   - **实现**: `SimulationDataFactory` 负责生产 `EnergyData` 对象，内部处理了电压浮动、功率因数计算及故障模拟等细节。调用者只需传入设备信息，无需关心数据具体的生成算法。
   - **核心位置**: `src/main/java/zxylearn/smart_cems_server/factory/SimulationDataFactory.java`

## 6. 核心功能
- **数据模拟**: 后端定时任务每 5 秒生成一次能耗数据，模拟真实物理环境波动。
- **异常告警**: 自动检测功率过载或电压异常，并生成告警记录。
- **统计分析**: 提供设备历史趋势和建筑用电占比统计接口。
