<template>
  <div class="promotion-tag">
    <span class="tag" :class="promotion.type">
      <span class="tag-icon" v-if="promotion.type === 'flash_sale'">&#9889;</span>
      <span class="tag-icon" v-else-if="promotion.type === 'new'">&#10024;</span>
      <span class="tag-icon" v-else>&#127873;</span>
      {{ promotion.tag }}
    </span>
    <span v-if="promotion.discount" class="discount">{{ promotion.discount }}</span>
    <span v-if="promotion.saveAmount > 0" class="save">省 ¥{{ promotion.saveAmount.toFixed(2) }}</span>
  </div>
</template>

<script setup>
defineProps({
  promotion: {
    type: Object,
    required: true
  }
})
</script>

<style scoped>
.promotion-tag {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.12);
}

.tag-icon {
  font-size: 12px;
}

.tag.flash_sale {
  background: linear-gradient(135deg, #2563eb, #3b82f6);
  animation: tagPulse 2s ease-in-out infinite;
}

.tag.discount {
  background: linear-gradient(135deg, #ffa502, #ff9f43);
}

.tag.new {
  background: linear-gradient(135deg, #2ed573, #7bed9f);
}

@keyframes tagPulse {
  0%, 100% { box-shadow: 0 2px 6px rgba(37,99,235,0.3); }
  50% { box-shadow: 0 2px 12px rgba(37,99,235,0.5); }
}

.discount {
  display: inline-block;
  padding: 2px 8px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 12px;
  font-weight: 700;
  border-radius: 4px;
  border: 1px solid rgba(37,99,235,0.2);
}

.save {
  display: inline-block;
  padding: 2px 8px;
  background: rgba(255, 165, 2, 0.12);
  color: #ff7f11;
  font-size: 12px;
  font-weight: 700;
  border-radius: 4px;
  border: 1px solid rgba(255, 165, 2, 0.24);
}
</style>
