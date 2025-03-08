import { ref } from 'vue'
import { useLocalSwitch, useLocalSelect } from './utils'

export const isAlert = useLocalSwitch('jweb-alert', true)

export const confirmTypes = {
  box: '弹框',
  pop: '气饱'
}
export const confirmType = useLocalSelect('jweb-confirm-type', confirmTypes, 'box')

export const confirmTxt = ref('确认')

export const cancelTxt = ref('取消')
