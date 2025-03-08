import { addMockData, type Pageable, type Page } from '@/core/http-client'

const books = [
  {
    id: 1,
    name: '这是一本书名',
    category: 5,
    price: 1000,
    publish: '2016-05-03',
    r: '多的'
  },
  {
    id: 2,
    name: '故事集',
    category: 1,
    price: 1999,
    publish: '2018-11-11',
    r: '多的'
  }
]

Array.from({ length: 33 }, (v, i) => i).forEach((i) => {
  books.push({
    id: i + 3,
    name: `流行小说合集-第${i + 1}期`,
    category: 2,
    price: 2999,
    publish: `2022-05-12`,
    r: '多的'
  })
})

export const getBooks = (query: Pageable<any>) => {
  let size = query.size ?? 10
  let index = ((query.page ?? 1) - 1) * size
  let data = books.filter((item) => {
    return (
      (query.name ? item.name.includes(query.name) : true) &&
      (query.category ? item.category === query.category : true)
    )
  })

  return Promise.resolve({
    content: data.slice(index, index + size),
    totalElements: data.length
  })
}

export const initBookData = () => {}

export default {
  install: () => {
    addMockData('get', '/books', ({ data }) => getBooks(data))
    addMockData('post', '/books', ({ data }) => {
      data.id = books.length + 1
      books.push(data)
      return Promise.resolve(data)
    })
    addMockData('put', '/books', ({ data }) => {
      let index = books.findIndex((book) => book.id === data.id)
      books[index] = data
      return Promise.resolve(data)
    })
    addMockData('delete', '/books', ({ data }) => {
      let index = books.findIndex((book) => book.id === data.id)
      books.splice(index, 1)
      return Promise.resolve()
    })
  }
}
