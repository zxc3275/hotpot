<template>
  <div class="dashboard">
    <!-- Stat Cards -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in statsCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card" :style="{ borderLeft: `4px solid ${stat.color}` }">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-value">{{ stat.value }}</div>
            </div>
            <div class="stat-icon">
              <el-icon :size="40" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row 1 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="chart-title">近7天销售额趋势</span>
          </template>
          <div ref="salesChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="chart-title">订单状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts Row 2 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="chart-title">热销菜品排行</span>
          </template>
          <div ref="dishChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <span class="chart-title">订单时段分布</span>
          </template>
          <div ref="timeChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import request from '@/api/request'
import {
  ShoppingCartFull,
  ShoppingCart,
  Coin,
  TrendCharts
} from '@element-plus/icons-vue'

// ── Stat cards ──────────────────────────────────────────────
const statsCards = ref([
  { label: '总订单数',  value: 0,     icon: 'ShoppingCartFull', color: '#409EFF' },
  { label: '今日订单',  value: 0,     icon: 'ShoppingCart',     color: '#67C23A' },
  { label: '总销售额',  value: '¥0', icon: 'Coin',            color: '#E6A23C' },
  { label: '今日销售额', value: '¥0', icon: 'TrendCharts',     color: '#F56C6C' }
])

// ── Chart refs ──────────────────────────────────────────────
const salesChartRef  = ref(null)
const statusChartRef = ref(null)
const dishChartRef   = ref(null)
const timeChartRef   = ref(null)

let salesChart  = null
let statusChart = null
let dishChart   = null
let timeChart   = null

// Store real data
const chartData = ref({
  dailySales: [],
  statusDistribution: [],
  topDishes: [],
  hourlyDistribution: []
})

const fetchStats = async () => {
  try {
    const res = await request.get('/admin/dashboard/stats')
    if (res.code === 200) {
      const d = res.data
      statsCards.value[0].value = d.totalOrders ?? 0
      statsCards.value[1].value = d.todayOrders ?? 0
      statsCards.value[2].value = '¥' + Number(d.totalSales || 0).toLocaleString()
      statsCards.value[3].value = '¥' + Number(d.todaySales || 0).toLocaleString()
      // Store chart data
      chartData.value = {
        dailySales: d.dailySales || [],
        statusDistribution: d.statusDistribution || [],
        topDishes: d.topDishes || [],
        hourlyDistribution: d.hourlyDistribution || []
      }
      // Refresh charts with real data
      refreshCharts()
    }
  } catch {
    statsCards.value.forEach(c => c.value = '加载失败')
  }
}

// ── Chart options (real data) ────────────────────────────────
const statusLabels = { 0:'待支付', 1:'已支付', 2:'制作中', 3:'已上菜', 4:'已完成', '-1':'已取消' }
const statusColors = { 0:'#909399', 1:'#409EFF', 2:'#E6A23C', 3:'#409EFF', 4:'#67C23A', '-1':'#F56C6C' }

const getSalesChartOption = () => {
  const d = chartData.value.dailySales
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: d.map(i => i.date) },
    yAxis: { type: 'value' },
    series: [{
      name: '销售额', type: 'line', smooth: true,
      data: d.map(i => (i.amount / 100).toFixed(0)),
      areaStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1,[
        { offset: 0, color: 'rgba(64,158,255,0.35)' },
        { offset: 1, color: 'rgba(64,158,255,0.02)' }
      ])},
      itemStyle: { color: '#409EFF' }
    }]
  }
}

const getStatusChartOption = () => {
  const d = chartData.value.statusDistribution
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      name: '订单状态', type: 'pie', radius: ['42%','72%'], center: ['50%','45%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
      data: d.map(i => ({
        value: i.count,
        name: statusLabels[i.status] || '未知',
        itemStyle: { color: statusColors[i.status] || '#909399' }
      }))
    }]
  }
}

const getDishChartOption = () => {
  const d = chartData.value.topDishes
  return {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: d.map(i => i.name).reverse() },
    series: [{
      name: '销量', type: 'bar',
      data: d.map(i => i.sales).reverse(),
      itemStyle: {
        borderRadius: [0,4,4,0],
        color: new echarts.graphic.LinearGradient(0,0,1,0,[
          { offset: 0, color: '#409EFF' }, { offset: 1, color: '#A0CFFF' }
        ])
      }
    }]
  }
}

const getTimeChartOption = () => {
  const d = chartData.value.hourlyDistribution
  const hours = ['0:00','1:00','2:00','3:00','4:00','5:00','6:00','7:00','8:00','9:00','10:00','11:00','12:00','13:00','14:00','15:00','16:00','17:00','18:00','19:00','20:00','21:00','22:00','23:00']
  const map = {}
  d.forEach(i => { map[i.hour] = i.count })
  const data = hours.map(h => map[parseInt(h)] || 0)
  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: hours },
    yAxis: { type: 'value' },
    series: [{
      name: '订单数', type: 'line', smooth: true, data,
      areaStyle: { color: new echarts.graphic.LinearGradient(0,0,0,1,[
        { offset: 0, color: 'rgba(103,194,58,0.3)' },
        { offset: 1, color: 'rgba(103,194,58,0.02)' }
      ])},
      itemStyle: { color: '#67C23A' }
    }]
  }
}

// ── Init helpers ────────────────────────────────────────────
const initCharts = () => {
  if (salesChartRef.value && !salesChart) {
    salesChart = echarts.init(salesChartRef.value)
  }
  if (statusChartRef.value && !statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }
  if (dishChartRef.value && !dishChart) {
    dishChart = echarts.init(dishChartRef.value)
  }
  if (timeChartRef.value && !timeChart) {
    timeChart = echarts.init(timeChartRef.value)
  }
}

const refreshCharts = () => {
  salesChart?.setOption(getSalesChartOption(), true)
  statusChart?.setOption(getStatusChartOption(), true)
  dishChart?.setOption(getDishChartOption(), true)
  timeChart?.setOption(getTimeChartOption(), true)
}

const handleResize = () => {
  salesChart?.resize()
  statusChart?.resize()
  dishChart?.resize()
  timeChart?.resize()
}

// ── Lifecycle ───────────────────────────────────────────────
onMounted(async () => {
  await nextTick()
  initCharts()
  // Show loading/empty state before data arrives
  salesChart?.setOption(getSalesChartOption(), true)
  statusChart?.setOption(getStatusChartOption(), true)
  dishChart?.setOption(getDishChartOption(), true)
  timeChart?.setOption(getTimeChartOption(), true)
  await fetchStats()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  statusChart?.dispose()
  dishChart?.dispose()
  timeChart?.dispose()
})
</script>

<style scoped>
.dashboard { min-height: 100%; }

/* ── Stat cards ─────────────────────── */
.stats-row { margin-bottom: 20px; }

.stat-card {
  border-radius: 8px;
  transition: transform 0.2s;
}
.stat-card:hover { transform: translateY(-2px); }
.stat-card :deep(.el-card__body) { padding: 20px; }

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  letter-spacing: 0.5px;
}

/* ── Chart cards ────────────────────── */
.charts-row { margin-bottom: 20px; }

.chart-card { border-radius: 8px; }
.chart-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-box { width: 100%; height: 360px; }

/* ── Dark mode ──────────────────────── */
:global(.dark) .stat-label { color: #a3a6ad; }
:global(.dark) .stat-value { color: #e0e0e0; }
:global(.dark) .chart-title { color: #e0e0e0; }
:global(.dark) .chart-card :deep(.el-card__header) { border-color: #333; }
</style>
