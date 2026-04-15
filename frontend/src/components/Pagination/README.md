通用分页组件

Props:
- pageNum: 当前页码
- pageSize: 每页条数
- total: 总条数

Events:
- update:pageNum
- update:pageSize
- pagination({pageNum,pageSize})

示例：
<Pagination
  v-model:pageNum="query.pageNum"
  v-model:pageSize="query.pageSize"
  :total="total"
  @pagination="getList"
/>
