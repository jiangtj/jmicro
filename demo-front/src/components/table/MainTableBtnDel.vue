<template>
  <MainConfirm type="error" title="确认删除？">
    <MainButton type="danger" size="small">
      <slot>删除</slot>
    </MainButton>
  </MainConfirm>
</template>
