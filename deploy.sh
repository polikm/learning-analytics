#!/bin/bash
# ============================================
# 学情数据统计分析平台 - 一键部署脚本
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

# 检查 Docker 是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
        echo "  Ubuntu/Debian: curl -fsSL https://get.docker.com | sh"
        echo "  macOS: brew install --cask docker"
        exit 1
    fi
    if ! docker compose version &> /dev/null && ! docker-compose version &> /dev/null; then
        error "Docker Compose 未安装"
        exit 1
    fi
    ok "Docker 环境检查通过"
}

# 检查端口占用
check_ports() {
    local ports=(80 5432 6379 8080)
    local port_names=("前端(Nginx)" "PostgreSQL" "Redis" "后端(Spring Boot)")
    for i in "${!ports[@]}"; do
        if lsof -i ":${ports[$i]}" &> /dev/null 2>&1 || ss -tlnp 2>/dev/null | grep -q ":${ports[$i]}"; then
            warn "端口 ${ports[$i]} (${port_names[$i]}) 已被占用，可能会影响启动"
        fi
    done
}

# 启动服务
start() {
    info "开始部署学情数据统计分析平台..."
    check_docker
    check_ports

    info "构建并启动所有服务（首次构建需要较长时间）..."
    docker compose up -d --build

    echo ""
    info "等待服务启动..."
    sleep 5

    # 检查服务状态
    echo ""
    info "服务状态："
    docker compose ps

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
    echo -e "  ${YELLOW}提示:${NC} 后端服务启动需要等待约 30-60 秒"
    echo ""
}

# 停止服务
stop() {
    info "停止所有服务..."
    docker compose down
    ok "服务已停止"
}

# 重启服务
restart() {
    info "重启所有服务..."
    docker compose restart
    ok "服务已重启"
}

# 查看日志
logs() {
    docker compose logs -f --tail=100
}

# 查看状态
status() {
    docker compose ps
}

# 导入演示数据
init_data() {
    info "导入演示数据..."
    docker exec -i learning-postgres psql -U postgres -d learning_analytics < learning-analytics/doc/sql/init-data.sql
    ok "演示数据导入完成"
}

# 清理所有数据（危险操作）
clean() {
    warn "此操作将删除所有容器、镜像和数据卷！"
    read -p "确定要继续吗？(y/N): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        docker compose down -v --rmi all
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
