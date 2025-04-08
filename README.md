# Switch Branch Tool / 分支切换工具

[English](#english) | [中文](#chinese)

## English

### Description
A powerful tool for managing and switching Git branches efficiently in multi-module projects. This tool enables developers to switch branches across multiple modules with a single command, significantly improving workflow efficiency.

### Features
- One-click batch branch switching across multiple modules
- Support for switching all modules to the same branch
- Support for switching specific modules to different branches

### Requirements
- Java 11 or higher
- Gradle 7.0 or higher
- Git

### Installation
1. Clone the repository:
```bash
git clone [repository-url]
```

2. Build the project:
```bash
./gradlew build
```

### Usage
Batch Branch Switching:
```bash
# Switch all modules to a specific branch
./gradlew switchBranch -Pbranch=feature/new-feature

# Switch specific modules to different branches
./gradlew switchBranch -Pmodules=module1,module2 -Pbranches=feature1,feature2
```

### Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

### License
[License information will be added here]

---

## Chinese

### 项目描述
一个高效的多模块项目 Git 分支切换工具。该工具允许开发者通过单个命令同时切换多个模块的分支，显著提高工作效率。

### 功能特点
- 一键批量切换多个模块的分支
- 支持将所有模块切换到同一分支
- 支持将指定模块切换到不同分支

### 系统要求
- Java 11 或更高版本
- Gradle 7.0 或更高版本
- Git

### 安装说明
1. 克隆仓库：
```bash
git clone [仓库地址]
```

2. 构建项目：
```bash
./gradlew build
```

### 使用说明
批量切换分支：
```bash
# 将所有模块切换到指定分支
./gradlew switchBranch -Pbranch=feature/new-feature

# 将指定模块切换到不同分支
./gradlew switchBranch -Pmodules=module1,module2 -Pbranches=feature1,feature2
```

### 参与贡献
欢迎提交 Pull Request 来帮助改进项目！

### 许可证
[许可证信息将在这里添加] 