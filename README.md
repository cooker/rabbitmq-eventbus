 * direct 完全匹配routingKey 才路由
 * topic [`*`] 匹配，[`#`] 匹配，[`.`] 为分隔符
 * fanout 推送到所有绑定的队列

docker run -d --name rabbit -p 15672:15672 -p 5672:5672 rabbitmq

> 开启可视化管理

rabbitmq-plugins enable rabbitmq_management