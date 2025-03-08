import type { App } from 'vue'

const modules = import.meta.glob(
  ['@/views/*/index.js', '@/views/*/page.js', '@/views/*/index.ts', '@/views/*/page.ts'],
  {
    eager: true
  }
)

export default {
  install: (app: App) => {
    for (const path in modules) {
      app.use((modules[path] as any).default)
    }
  }
}
