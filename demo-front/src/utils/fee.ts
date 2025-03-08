export const formFormatter = {
  get: (v: string | number) => Number((Number(v) * 100).toFixed(0)),
  set: (v: string | number) => (Number(v) / 100).toFixed(2)
}

export const columnRender = (cell: number) => (cell / 100).toFixed(2)
