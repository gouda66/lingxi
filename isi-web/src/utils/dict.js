import useDictStore from '@/store/modules/dict'
import { getDicts } from '@/api/system/dict/data'

export function useDict(...args) {
  const res = ref({})
  return (() => {
    args.forEach((dictType, index) => {
      res.value[dictType] = []
      const dicts = useDictStore().getDict(dictType)
      if (dicts) {
        res.value[dictType] = dicts
      } else {
        getDicts(dictType).then(resp => {
          // 后端可能直接返回数组，也可能返回 { code: 1, data: [...] }
          let dataArray = resp
          if (resp && typeof resp === 'object' && 'data' in resp) {
            dataArray = resp.data
          }

          if (Array.isArray(dataArray)) {
            res.value[dictType] = dataArray.map(p => ({ label: p.dictLabel, value: p.dictValue, elTagType: p.listClass, elTagClass: p.cssClass }))
            useDictStore().setDict(dictType, res.value[dictType])
          } else {
            console.error(`字典 ${dictType} 数据格式错误:`, resp)
            res.value[dictType] = []
          }
        }).catch(error => {
          console.error(`加载字典 ${dictType} 失败:`, error)
          res.value[dictType] = []
        })
      }
    })
    return toRefs(res.value)
  })()
}
