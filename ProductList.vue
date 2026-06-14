<template>
  <div class="product-list">
    <!-- 面包屑 -->
    <div class="breadcrumb">
      <router-link to="/">首页</router-link>
      <span class="sep">/</span>
      <span class="current">全部商品</span>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <div class="filter-row">
        <span class="filter-label">分类：</span>
        <div class="filter-options">
          <button
            class="filter-tag"
            :class="{ active: selectedCategory === '' }"
            @click="handleCategoryChange('')"
          >全部</button>
          <button
            v-for="cat in categories"
            :key="cat"
            class="filter-tag"
            :class="{ active: selectedCategory === cat }"
            @click="handleCategoryChange(cat)"
          >{{ cat }}</button>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">排序：</span>
        <div class="filter-options">
          <button
            class="sort-tag"
            :class="{ active: sortBy === 'default' }"
            @click="handleSortChange('default')"
          >综合</button>
          <button
            class="sort-tag"
            :class="{ active: sortBy === 'price_asc' }"
            @click="handleSortChange('price_asc')"
          >价格 <span class="arrow-up">&#8593;</span></button>
          <button
            class="sort-tag"
            :class="{ active: sortBy === 'price_desc' }"
            @click="handleSortChange('price_desc')"
          >价格 <span class="arrow-down">&#8595;</span></button>
          <button
            class="sort-tag"
            :class="{ active: sortBy === 'sales' }"
            @click="handleSortChange('sales')"
          >销量</button>
        </div>
      </div>
    </div>

    <!-- 商品统计 -->
    <div class="list-header">
      <span class="result-count">
        共找到 <strong>{{ total }}</strong> 件商品 · 当前第 <strong>{{ currentPage }}</strong> 页 · 已发起 <strong>{{ requestCount }}</strong> 次请求
      </span>
    </div>

    <div v-if="loading" class="status-panel">商品加载中...</div>
    <div v-else-if="errorMessage" class="status-panel error">{{ errorMessage }}</div>

    <!-- 商品网格 -->
    <div v-if="!loading && products.length" class="products-grid">
      <div v-for="product in products" :key="product.id" class="product-card">
        <router-link :to="`/product/${product.id}`" class="card-img-wrap">
          <img :src="product.image" :alt="product.name" />
          <div class="card-tag" v-if="product.promotionPrice">促销</div>
        </router-link>
        <div class="card-body">
          <router-link :to="`/product/${product.id}`" class="card-title">{{ product.name }}</router-link>
          <p class="card-desc">{{ product.description }}</p>
          <div class="card-rating">
            <span class="stars">&#9733;&#9733;&#9733;&#9733;&#9734;</span>
            <span class="rating-count">{{ Math.floor(Math.random() * 5000) + 500 }}条评价</span>
          </div>
          <div class="card-price-row">
            <span v-if="product.promotionPrice" class="card-sale-price">
              <span class="yen">¥</span>{{ product.promotionPrice }}
            </span>
            <span v-else class="card-sale-price">
              <span class="yen">¥</span>{{ product.price }}
            </span>
            <span v-if="product.promotionPrice" class="card-original">¥{{ product.price }}</span>
          </div>
          <button class="card-cart-btn" @click="addToCart(product)">
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
            </svg>
            加入购物车
          </button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && !errorMessage && products.length === 0" class="empty-state">
      <div class="empty-icon">&#128269;</div>
      <p>暂无符合条件的商品</p>
      <button class="reset-btn" @click="handleCategoryChange('')">重置筛选</button>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button class="page-btn" :disabled="currentPage <= 1" @click="changePage(currentPage - 1)">
        &#10094; 上一页
      </button>
      <div class="page-numbers">
        <button
          v-for="page in displayPages"
          :key="page"
          class="page-num"
          :class="{ active: page === currentPage }"
          @click="changePage(page)"
        >{{ page }}</button>
      </div>
      <button class="page-btn" :disabled="currentPage >= totalPages" @click="changePage(currentPage + 1)">
        下一页 &#10095;
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCartStore } from '../stores/cart'
import { getProducts } from '../api/product'

const cartStore = useCartStore()
const products = ref([])
const categories = ref(['电子产品', '服装', '食品', '家居'])
const selectedCategory = ref('')
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)
const totalPages = ref(1)
const requestCount = ref(0)
const loading = ref(false)
const errorMessage = ref('')

const displayPages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)
  if (end - start < 4) start = Math.max(1, end - 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

async function loadProducts() {
  loading.value = true
  errorMessage.value = ''
  requestCount.value += 1

  try {
    const res = await getProducts({
      page: currentPage.value,
      size: pageSize.value,
      category: selectedCategory.value,
      sortBy: sortBy.value
    })

    products.value = res.data || []
    total.value = Number(res.page?.total || 0)
    totalPages.value = Number(res.page?.pages || 1)
    currentPage.value = Number(res.page?.current || currentPage.value)
  } catch (error) {
    products.value = []
    total.value = 0
    totalPages.value = 1
    errorMessage.value = error.message || '商品加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

function handleCategoryChange(category = selectedCategory.value) {
  selectedCategory.value = category
  currentPage.value = 1
  loadProducts()
}

function handleSortChange(sort) {
  sortBy.value = sort
  currentPage.value = 1
  loadProducts()
}

function changePage(page) {
  if (page < 1 || page > totalPages.value || page === currentPage.value) return
  currentPage.value = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function addToCart(product) {
  cartStore.addToCart(product)
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
/* ========== 面包屑 ========== */
.breadcrumb {
  font-size: 13px;
  color: #a4b0be;
  margin-bottom: 16px;
}

.breadcrumb a {
  color: #a4b0be;
  transition: color 0.2s;
}

.breadcrumb a:hover {
  color: #2563eb;
}

.breadcrumb .sep {
  margin: 0 8px;
}

.breadcrumb .current {
  color: #2f3542;
}

/* ========== 筛选区域 ========== */
.filter-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.filter-row {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #f1f2f6;
}

.filter-row:last-child {
  border-bottom: none;
}

.filter-label {
  font-size: 14px;
  color: #2f3542;
  font-weight: 600;
  min-width: 50px;
  padding-top: 4px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag,
.sort-tag {
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 13px;
  background: #f1f2f6;
  color: #2f3542;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.filter-tag:hover,
.sort-tag:hover {
  background: #dbeafe;
  color: #2563eb;
}

.filter-tag.active,
.sort-tag.active {
  background: #2563eb;
  color: #fff;
  border-color: #2563eb;
}

.arrow-up,
.arrow-down {
  font-size: 10px;
}

/* ========== 列表头部 ========== */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.result-count {
  font-size: 14px;
  color: #a4b0be;
}

.result-count strong {
  color: #2563eb;
  font-weight: 700;
}

.status-panel {
  margin-bottom: 16px;
  padding: 16px 18px;
  border-radius: 12px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 14px;
  font-weight: 500;
}

.status-panel.error {
  background: #fff5f5;
  color: #e03131;
}

/* ========== 商品网格 ========== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.12);
}

.card-img-wrap {
  position: relative;
  display: block;
  height: 220px;
  overflow: hidden;
}

.card-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.product-card:hover .card-img-wrap img {
  transform: scale(1.06);
}

.card-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 3px 10px;
  background: #2563eb;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  border-radius: 4px;
}

.card-body {
  padding: 14px;
}

.card-title {
  display: block;
  font-size: 14px;
  color: #2f3542;
  font-weight: 500;
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}

.card-title:hover {
  color: #2563eb;
}

.card-desc {
  font-size: 12px;
  color: #a4b0be;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
}

.stars {
  color: #ffa502;
  font-size: 12px;
  letter-spacing: -1px;
}

.rating-count {
  font-size: 12px;
  color: #a4b0be;
}

.card-price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 12px;
}

.card-sale-price {
  font-size: 22px;
  font-weight: 800;
  color: #2563eb;
}

.yen {
  font-size: 14px;
}

.card-original {
  font-size: 13px;
  color: #a4b0be;
  text-decoration: line-through;
}

.card-cart-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  color: #fff;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  transition: all 0.3s;
}

.card-cart-btn:hover {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  box-shadow: 0 4px 12px rgba(37,99,235,0.3);
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.empty-state p {
  color: #a4b0be;
  font-size: 16px;
  margin-bottom: 20px;
}

.reset-btn {
  padding: 10px 30px;
  background: #2563eb;
  color: #fff;
  border-radius: 24px;
  font-size: 14px;
  transition: all 0.3s;
}

.reset-btn:hover {
  background: #3b82f6;
}

/* ========== 分页 ========== */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 40px;
  padding-bottom: 20px;
}

.page-btn {
  padding: 8px 18px;
  background: #fff;
  color: #2f3542;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #2563eb;
  color: #fff;
}

.page-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-num {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  font-size: 14px;
  color: #2f3542;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
  transition: all 0.2s;
}

.page-num:hover {
  background: #dbeafe;
  color: #2563eb;
}

.page-num.active {
  background: #2563eb;
  color: #fff;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .filter-row {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
