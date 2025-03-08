/// <reference types="vite/client" />
/// <reference types="vite-plugin-pwa/vue" />

declare module '~icons/*' {
  import { FunctionalComponent, SVGAttributes } from 'vue'
  const component: FunctionalComponent<SVGAttributes>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_BASE_URL: string
  readonly VITE_BASE_URL_CACHE: boolean
  readonly VITE_BASE_URL_MODIFY: boolean
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
