export type TagType = 'primary' | 'success' | 'warning' | 'info' | 'danger' | undefined

export interface DictOption<T = number | string> {
  label: string
  value: T
  tagType?: TagType
  color?: string
}

export const userTypeOptions: DictOption<number>[] = [
  { label: '管理员', value: 0, tagType: 'primary' },
  { label: '教师', value: 1, tagType: 'success' },
  { label: '学生', value: 2, tagType: 'info' },
  { label: '安保', value: 3, tagType: 'warning' }
]

export const deviceStatusOptions: DictOption<number>[] = [
  { label: '正常', value: 0, tagType: 'success' },
  { label: '故障', value: 1, tagType: 'danger' },
  { label: '维修', value: 2, tagType: 'warning' },
  { label: '报废', value: 3, tagType: 'info' }
]

export const assetStatusOptions: DictOption<number>[] = [
  { label: '在用', value: 0, tagType: 'success' },
  { label: '闲置', value: 1, tagType: 'info' },
  { label: '报废', value: 2, tagType: 'danger' },
  { label: '维修', value: 3, tagType: 'warning' }
]

export const visitorStatusOptions: DictOption<number>[] = [
  { label: '待审批', value: 0, tagType: 'warning' },
  { label: '已通过', value: 1, tagType: 'success' },
  { label: '已拒绝', value: 2, tagType: 'danger' },
  { label: '已签到', value: 3, tagType: 'primary' },
  { label: '已签退', value: 4, tagType: 'info' }
]

export const warningTypeOptions: DictOption<number>[] = [
  { label: '安全', value: 1, tagType: 'primary' },
  { label: '设备', value: 2, tagType: 'warning' },
  { label: '访客', value: 3, tagType: 'info' },
  { label: '其他', value: 4, tagType: 'info' }
]

// F123: 预警级别统一颜色：低蓝/灰、中黄、高橙红、严重红色
export const warningLevelOptions: DictOption<number>[] = [
  { label: '低', value: 1, tagType: 'info', color: '#909399' },
  { label: '中', value: 2, tagType: 'warning', color: '#E6A23C' },
  { label: '高', value: 3, tagType: 'danger', color: '#F56C6C' },
  { label: '严重', value: 4, tagType: 'danger', color: '#D93026' }
]

export const warningRecordStatusOptions: DictOption<number>[] = [
  { label: '待处理', value: 0, tagType: 'warning' },
  { label: '处理中', value: 1, tagType: 'primary' },
  { label: '已处理', value: 2, tagType: 'success' },
  { label: '误报', value: 3, tagType: 'info' }
]

export function dictLabel<T = number | string>(options: DictOption<T>[], value: unknown, fallback = ''): string {
  const item = options.find((x) => String(x.value) === String(value))
  if (item) return item.label
  return value === undefined || value === null || value === '' ? fallback : String(value)
}

export function dictTagType<T = number | string>(options: DictOption<T>[], value: unknown): TagType {
  return options.find((x) => String(x.value) === String(value))?.tagType
}

export function dictColor<T = number | string>(options: DictOption<T>[], value: unknown): string | undefined {
  return options.find((x) => String(x.value) === String(value))?.color
}
