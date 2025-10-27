# LucaOneGUI

这是一款能利用阿里云官方的LucaOne接口进行嵌入的软件, 官方详情页: https://university.aliyun.com/lesson/lucaone . 官方API不支持大于500的fasta文件进行嵌入, 该软件默认将fasta文件进行分片上传至OSS, 并通过API请求传给LucaOne进行嵌入, 解放你的双手吧.

## 特性

* 大文件 (>500) 的fasta文件将被自动分片上传到OOS并通过API运行嵌入
* 嵌入参数设置
* 嵌入进度查看
* 设置并保存 API Key
* 多语言支持
* 待实现: 如果官方后续出下游任务的API, 将考虑支持

## 快速开始

### 需求

* 需要先安装 Java 21: https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html
* 确保阿里云OSS已开通, 并且已创建并发布一个LucaOne的工作流: https://eduplatform-sp.console.aliyun.com/comfyui-management. 工作流可以用储存库中的 `LucaOne.json` 文件导入, 在工作流界面运行成功一次后即可发布.

### 运行软件

1. 请先将 Java 21 添加到 PATH, 或用绝对路径运行命令. 示例: `java -jar LucaOneGUI.jar`, `C:\Program Files\Java\jdk-21\bin\java.exe -jar LucaOneGUI.jar`, `/usr/lib/jvm/java-21-openjdk-amd64/java -jar LucaOneGUI.jar`
2. 出现软件界面后, 请先点击 "设置" 按钮, 填写拥有 OSS 权限的Access Key 和 Access Secret Key, 然后点击 "保存" 按钮

   | OSS Access Key        | 拥有 OSS 权限的Access Key                                                                                    |
   |-----------------------|---------------------------------------------------------------------------------------------------------|
   | OSS Access Secret Key | 拥有 OSS 权限的OSS Access Secret Key                                                                         |
   | OSS Endpoint          | OSS访问地址, 如: oss-cn-shenzhen.aliyuncs.com                                                                |
   | OSS Region            | OSS地域, 如: cn-shenzhen                                                                                   |
   | OSS Bucket Name       | 软件不会自动创建OSS Bucket, 请自行创建Bucket并填写Bucket名称                                                              |
   | LucaOne AK            | 通过智作工坊-开发者中心-应用管理界面获得:https://eduplatform-sp.console.aliyun.com/developer-center/application-management |
   | LucaOne SK            | 通过智作工坊-开发者中心-应用管理界面获得:https://eduplatform-sp.console.aliyun.com/developer-center/application-management |
   | Workflow ID           | 通过智作工坊-工作流管理界面获得https://eduplatform-sp.console.aliyun.com/comfyui-management                            |
   | Workflow Name         | 通过智作工坊-工作流管理界面获得 (需要先发布工作流并为一个版本命名别名)https://eduplatform-sp.console.aliyun.com/comfyui-management       |

3. 转到 `提交` 页面, 填写参数, 选择 `.fasta` 文件并点击 `提交` 按钮
4. 转到 `历史记录` 页面, 查看运行状态
5. 嵌入结果将会在软件轮询到状态变为 "成功" 后自动下载至当前文件夹下.
> ❗ 注意: 由于嵌入成功后, 结果并不会永久保存, 本软件并未设计关闭后重新恢复结果轮询. 嵌入时请勿关闭软件, 关闭将丢失进度.

## 注意事项

经测试, 一个包含 10 个 CDS 序列的 fasta 文件嵌入, 平均 15.5 秒一条. 也就是说 500 条数据将计算 2.15 小时, 官方的价格为 20.5 元 1 小时, 500 条 CDS 序列嵌入大约需要 44 元.

考虑到高昂的价格, 官方 API 限制最大上传数量为 500 条序列是有道理的, 您可能需要定期查看结果并考虑是否要调整参数, 所以请谨慎决定是否要使用这个软件进行批量嵌入. 