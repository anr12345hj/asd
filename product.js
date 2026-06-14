import request from '../utils/request.js'

export function getProducts(params) {
  return request({
    url: '/products',
    method: 'get',
    params,
    mock: true
  })
}

export function getProductById(id) {
  return request({
    url: `/products/${id}`,
    method: 'get',
    mock: true
  })
}
