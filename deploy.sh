#!/bin/bash
# ============================================
# 学情数据统计分析平台 - 一键部署脚本
# 兼容 Linux / macOS / Git Bash (Windows)
# ============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 打印带颜色的信息
info()  { echo -e "${BLUE}[INFO]${NC} $1"; }
ok()    { echo -e "${GREEN}[OK]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检测操作系统
detect_os() {
    case "$(uname -s)" in
        Linux*)     OS="Linux" ;;
        Darwin*)    OS="macOS" ;;
        MINGW*|MSYS*|CYGWIN*) OS="Windows" ;;
        *)          OS="Unknown" ;;
    esac
}

# 检查 Docker 是否安装且运行
check_docker() {
    detect_os

    # 1. 检查 docker 命令是否存在
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker Desktop"
        echo ""
        case "$OS" in
            Windows)
                echo "  下载地址: https://www.docker.com/products/docker-desktop/"
                echo "  安装后请重启电脑，并确保 Docker Desktop 已启动"
                ;;
            macOS)
                echo "  安装方式: brew install --cask docker"
                echo "  或下载: https://www.docker.com/products/docker-desktop/"
                ;;
            *)
                echo "  Ubuntu/Debian: curl -fsSL https://get.docker.com | sh"
                ;;
        esac
        exit 1
    fi

    # 2. 检查 Docker daemon 是否运行
    if ! docker info &> /dev/null; then
        error "Docker daemon 未运行"
        echo ""
        case "$OS" in
            Windows)
                echo "  请执行以下操作："
                echo "  1. 打开 Docker Desktop 应用（从开始菜单或桌面快捷方式）"
                echo "  2. 等待 Docker Desktop 状态栏图标变为绿色（表示已就绪）"
                echo "  3. 重新运行本脚本"
                ;;
            macOS)
                echo "  请执行以下操作："
                echo "  1. 打开 Docker Desktop 应用"
                echo "  2. 等待状态栏鲸鱼图标变为稳定状态"
                echo "  3. 重新运行本脚本"
                ;;
            *)
                echo "  请执行: sudo systemctl start docker"
                ;;
        esac
        exit 1
    fi

    # 3. 检查 Docker Compose 是否可用
    if docker compose version &> /dev/null; then
        COMPOSE_CMD="docker compose"
    elif command -v docker-compose &> /dev/null; then
        COMPOSE_CMD="docker-compose"
    else
        error "Docker Compose 未安装"
        echo "  Docker Desktop 已内置 Compose，请确认 Docker Desktop 版本为最新"
        exit 1
    fi

    ok "Docker 环境检查通过 ($OS, $(docker --version | grep -oP '\d+\.\d+\.\d+' | head -1))"
}

# 检查端口占用
check_ports() {
    local ports=(80 5432 6379 8080)
    local port_names=("前端(Nginx)" "PostgreSQL" "Redis" "后端(Spring Boot)")
    for i in "${!ports[@]}"; do
        local port="${ports[$i]}"
        local name="${port_names[$i]}"
        local in_use=false

        case "$OS" in
            Windows)
                # Windows: 使用 netstat 检查端口
                if netstat -ano 2>/dev/null | grep -q ":${port} .*LISTENING"; then
                    in_use=true
                fi
                ;;
            *)
                if lsof -i ":${port}" &> /dev/null 2>&1 || ss -tlnp 2>/dev/null | grep -q ":${port}"; then
                    in_use=true
                fi
                ;;
        esac

        if [ "$in_use" = true ]; then
            warn "端口 ${port} (${name}) 已被占用，可能会影响启动"
        fi
    done
}

# 启动服务
start() {
    info "开始部署学情数据统计分析平台..."
    check_docker
    check_ports

    echo ""
    info "=========================================="
    info "  第 1 步：构建镜像（首次约 3-8 分钟）"
    info "=========================================="
    echo ""
    warn "首次构建需要下载基础镜像和依赖，请耐心等待..."
    warn "如果网络较慢，后端构建可能需要 10+ 分钟"
    echo ""

    # 先构建镜像（前台输出，让用户看到进度）
    $COMPOSE_CMD build 2>&1
    local build_result=$?

    if [ $build_result -ne 0 ]; then
        error "镜像构建失败！请检查上方错误信息"
        echo ""
        echo "常见问题："
        echo "  1. 网络问题：无法下载 Maven 依赖或 npm 包，请检查网络/代理"
        echo "  2. 磁盘空间不足：Docker 镜像需要约 3GB 空间"
        echo "  3. 内存不足：Maven 编译需要至少 2GB 可用内存"
        exit 1
    fi

    echo ""
    ok "镜像构建成功！"
    echo ""
    info "=========================================="
    info "  第 2 步：启动服务"
    info "=========================================="
    echo ""

    # 后台启动服务
    $COMPOSE_CMD up -d

    echo ""
    info "等待服务启动（后端约需 30-60 秒）..."
    sleep 8

    # 检查服务状态
    echo ""
    info "服务状态："
    $COMPOSE_CMD ps

    echo ""
    echo "=========================================="
    ok "部署完成！"
    echo "=========================================="
    echo ""
    echo -e "  ${GREEN}前端访问:${NC}  http://localhost"
    echo -e "  ${GREEN}后端 API:${NC}  http://localhost:8080"
    echo -e "  ${GREEN}API 文档:${NC}  http://localhost/swagger-ui/"
    echo ""
    echo -e "  ${BLUE}默认账号:${NC}"
    echo "    管理员: admin / admin123"
    echo "    教  师: teacher01 / teacher123"
    echo "    家  长: parent01 / parent123"
    echo "    学  生: student01 / student123"
    echo ""
    echo -e "  ${YELLOW}提示:${NC}"
    echo "    - 后端服务启动需要等待约 30-60 秒"
    echo "    - 查看实时日志: ./deploy.sh logs"
    echo "    - 查看服务状态: ./deploy.sh status"
    echo ""
}

# 停止服务
stop() {
    check_docker
    info "停止所有服务..."
    $COMPOSE_CMD down
    ok "服务已停止"
}

# 重启服务
restart() {
    check_docker
    info "重启所有服务..."
    $COMPOSE_CMD restart
    ok "服务已重启"
}

# 查看日志
logs() {
    check_docker
    $COMPOSE_CMD logs -f --tail=100
}

# 查看状态
status() {
    check_docker
    $COMPOSE_CMD ps
}

# 导入演示数据
init_data() {
    check_docker
    info "导入演示数据..."
    docker exec -i learning-postgres psql -U postgres -d learning_analytics < learning-analytics/doc/sql/init-data.sql
    ok "演示数据导入完成"
}

# 清理所有数据（危险操作）
clean() {
    check_docker
    warn "此操作将删除所有容器、镜像和数据卷！"
    read -p "确定要继续吗？(y/N): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        $COMPOSE_CMD down -v --rmi all
        ok "清理完成"
    else
        info "已取消"
    fi
}

# 使用说明
usage() {
    echo "学情数据统计分析平台 - Docker 部署管理脚本"
    echo ""
    echo "用法: $0 [命令]"
    echo ""
    echo "命令:"
    echo "  start       一键构建并启动所有服务（默认）"
    echo "  stop        停止所有服务"
    echo "  restart     重启所有服务"
    echo "  logs        查看实时日志"
    echo "  status      查看服务状态"
    echo "  init-data   导入演示数据"
    echo "  clean       清理所有容器、镜像和数据卷"
    echo "  help        显示此帮助信息"
    echo ""
}

# 主入口
case "${1:-start}" in
    start)     start ;;
    stop)      stop ;;
    restart)   restart ;;
    logs)      logs ;;
    status)    status ;;
    init-data) init_data ;;
    clean)     clean ;;
    help|-h|--help) usage ;;
    *)         error "未知命令: $1"; usage; exit 1 ;;
esac
