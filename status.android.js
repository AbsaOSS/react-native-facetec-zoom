
const upFirstLowRest = str => str[0].toUpperCase() + str.slice(1).toLowerCase()
// eslint-disable-next-line camelcase
const toPascalCase = snake_case => snake_case && snake_case.split('_').reduce((pascal, part) => pascal + upFirstLowRest(part), '')

export default toPascalCase
