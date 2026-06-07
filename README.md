<div align="center">

# 男妃 AI 超级智能体应用平台

**基于 Spring AI + ReAct Agent 的全能 AI 智能体平台**

集 AI 恋爱大师与 AI 超级智能体于一体，通过多工具调用、RAG 知识库增强、MCP 协议扩展，打造沉浸式流式对话体验。

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue)](https://spring.io/projects/spring-ai)
[![Spring AI Alibaba](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.1.2-orange)](https://java2ai.com/)
[![Vue](https://img.shields.io/badge/Vue-3-42b883?logo=vuedotjs)](https://vuejs.org/)
[![Vite](https://img.shields.io/badge/Vite-4-646cff?logo=vite)](https://vitejs.dev/)

[在线体验](https://agent.nanfei.chat/) · [GitHub 仓库](https://github.com/nanfei892/ai-agent-lasted)

</div>

---

## 项目简介

男妃 AI 超级智能体应用平台是一个面向 AI 对话场景的全栈应用，包含 **AI 恋爱大师** 和 **AI 超级智能体（Manus）** 两大核心模块。

平台基于 Spring AI Alibaba 对接通义千问大模型，通过自研的 **ReAct Agent 框架**（Think → Act 循环）实现多步推理与工具调用，并结合 RAG 检索增强、MCP 协议扩展、文件级对话记忆等能力，让 AI 不仅能「聊天」，还能「做事」——搜索网页、抓取内容、操作文件、生成 PDF、执行终端命令等。

---

## 项目介绍

1.首页：可选AI恋爱咨询与全能AI助手。

![](https://github.com/nanfei892/ai-agent-lasted/blob/master/images/1%E9%A6%96%E9%A1%B5.png)

2. AI 恋爱大师：基于 RAG 知识库增强的情感顾问，支持多轮对话、流式输出、对话记忆，为用户提供专业的恋爱建议。

![](https://github.com/nanfei892/ai-agent-lasted/blob/master/images/2%E6%81%8B%E7%88%B1%E5%A4%A7%E5%B8%88.png)

3. AI 超级智能体（Manus）：基于 ReAct 模式的全能 Agent，可自动选择并调用多种工具，逐步完成用户提出的复杂任务。

![](https://github.com/nanfei892/ai-agent-lasted/blob/master/images/3%E6%99%BA%E8%83%BD%E4%BD%93.png)

---

## 核心功能

| 功能 | 说明 |
| --- | --- |
| AI 恋爱大师 | 专业情感顾问，结合 RAG 知识库给出专业恋爱建议 |
| AI 超级智能体 | 基于 ReAct Agent 框架，支持多步推理 + 工具调用的全能助手 |
| SSE 流式输出 | 基于 Reactor Flux + SseEmitter，AI 执行过程实时推送前端展示 |
| ReAct Agent 框架 | 自研 BaseAgent → ReAcAgent → ToolCallAgent 分层架构 |
| 多工具调用 | 支持网页搜索、网页抓取、文件操作、PDF 生成、终端执行、资源下载等 |
| MCP 协议扩展 | 通过 MCP 客户端接入外部工具服务，如图片搜索等 |
| RAG 知识库增强 | 基于 Spring AI VectorStore + 文档加载器，实现检索增强问答 |
| 查询重写 | 对用户输入进行改写优化，提升 RAG 检索效果 |
| 对话记忆 | 基于 Kryo 序列化的文件级对话记忆，支持多会话独立上下文 |
| 结构化事件推送 | Agent 执行过程以结构化 JSON 事件（思考/工具调用/结果/回答）流式推送 |
| 多种 SSE 接入方式 | 支持 Flux、ServerSentEvent、SseEmitter 三种流式响应模式 |
| API 文档 | 集成 Knife4j，提供完整的接口文档和在线调试能力 |

---

## 项目亮点

### 1. 自研 ReAct Agent 框架

项目实现了完整的 ReAct（Reasoning and Action）智能体架构：

- **BaseAgent**：管理 Agent 状态、多步循环、同步/流式两种运行方式
- **ReAcAgent**：实现 Think → Act 的循环推理模式
- **ToolCallAgent**：支持工具调用的具体实现，禁用 Spring AI 内置工具调用，自主维护上下文
- **YuManus**：全能超级智能体，可组合多种工具完成复杂任务

```text
用户输入 → Agent 思考（think） → 选择工具 → 执行工具（act） → 观察结果 → 继续思考 → ... → 最终回答
```

### 2. SSE 流式结构化推送

不同于简单的文本流输出，Agent 执行过程中推送结构化 JSON 事件：

- `AGENT_START`：Agent 开始执行
- `STEP_START`：每一步开始
- `THINK`：AI 的思考内容
- `TOOL_CALL`：工具调用信息（工具名 + 参数）
- `TOOL_RESULT`：工具执行结果
- `ANSWER`：最终回答
- `DONE`：执行结束

前端可据此渲染完整的 Agent 执行轨迹，让用户看到 AI 的「思考过程」。

### 3. RAG 检索增强问答

恋爱大师模块集成了完整的 RAG 流程：

- Markdown 文档加载与拆分
- 向量存储（支持 PGVector）
- 查询重写优化
- QuestionAnswerAdvisor 知识库问答
- 支持自定义 RAG 顾问和云端知识库服务

### 4. 丰富的工具生态

| 工具 | 能力 |
| --- | --- |
| WebSearchTool | 网页搜索（基于 SearchAPI） |
| WebScrapingTool | 网页内容抓取（基于 jsoup） |
| FileOperationTool | 文件读写操作 |
| PDFGenerationTool | PDF 文档生成（基于 iText） |
| TerminalOperationTool | 终端命令执行 |
| ResourceDownloadTool | 网络资源下载 |
| TerminateTool | Agent 主动终止 |
| MCP 工具 | 通过 MCP 协议接入外部服务（如图片搜索） |

---

## 技术栈

### 后端

| 技术 | 用途 |
| --- | --- |
| Java 21 | 后端主要开发语言 |
| Spring Boot 3.5 | Web 服务、配置管理、依赖注入 |
| Spring AI 1.1.2 | AI 服务编排、ChatClient、工具调用、向量存储 |
| Spring AI Alibaba | 对接通义千问（DashScope）大模型 |
| Reactor | 流式响应处理（Flux） |
| Spring AI MCP Client | MCP 协议客户端，扩展外部工具 |
| Hutool | 通用工具类库 |
| Kryo | 高性能序列化（对话记忆持久化） |
| jsoup | 网页内容抓取与解析 |
| iText | PDF 文档生成 |
| Knife4j | 后端接口文档与在线调试 |

### 前端

| 技术 | 用途 |
| --- | --- |
| Vue 3 | 前端框架 |
| Vite | 前端构建工具 |
| Vue Router | 页面路由 |
| Axios | HTTP 请求 |
| EventSource | SSE 流式消息接收 |

---

## 项目架构

```text
ai-agent-lasted/
├── src/main/java/com/zsm/aiagentlasted/
│   ├── advisor/          # 自定义 Advisor（日志记录、Re2 重读）
│   ├── agent/            # Agent 框架（BaseAgent → ReAcAgent → ToolCallAgent → YuManus）
│   │   └── model/        # Agent 状态、事件类型等模型
│   ├── app/              # 应用层（LoveApp 恋爱大师）
│   ├── chatmemory/       # 对话记忆（基于文件的 Kryo 序列化）
│   ├── config/           # 配置（跨域等）
│   ├── constant/         # 常量定义
│   ├── controller/       # 接口层（同步、SSE、Manus）
│   ├── rag/              # RAG 检索增强（文档加载、向量存储、查询重写）
│   └── tools/            # 工具定义（搜索、抓取、文件、PDF、终端等）
├── yu-ai-agent-frontend/ # Vue 3 前端
│   ├── src/
│   │   ├── api/          # API 请求封装
│   │   ├── components/   # 组件（聊天室、Agent 轨迹卡片等）
│   │   ├── utils/        # Agent 流式解析工具
│   │   └── views/        # 页面（首页、恋爱大师、超级智能体）
├── nanfei-image-search-mcp/  # MCP 图片搜索服务（独立子项目）
└── doc/                  # 项目文档
```

---

## 作者

**nanfei892**

- GitHub：[@nanfei892](https://github.com/nanfei892)
- 邮箱：nanfei892@gmail.com

如果这个项目对你有帮助，欢迎 Star。
