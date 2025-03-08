import { ref } from 'vue'
import { useSessionStorage } from '@vueuse/core'
import type { Ref } from 'vue'

export interface UserInfo {
  owner: string
  name: string
  createdTime: string
  updatedTime: string
  id: string
  type: string
  password: string
  passwordSalt: string
  displayName: string
  firstName: string
  lastName: string
  avatar: string
  permanentAvatar: string
  email: string
  phone: string
  location: string
  address: string[]
  affiliation: string
  title: string
  idCardType: string
  idCard: string
  homepage: string
  bio: string
  tag: string
  region: string
  language: string
  gender: string
  birthday: string
  education: string
  score: number
  karma: number
  ranking: number
  signupApplication: string
  hash: string
  preHash: string
  createdIp: string
  lastSigninTime: string
  lastSigninIp: string
  github: string
  google: string
  qq: string
  wechat: string
  facebook: string
  dingtalk: string
  weibo: string
  gitee: string
  linkedin: string
  wecom: string
  lark: string
  gitlab: string
  adfs: string
  baidu: string
  alipay: string
  casdoor: string
  infoflow: string
  apple: string
  azuread: string
  slack: string
  steam: string
  bilibili: string
  okta: string
  douyin: string
  custom: string
  ldap: string
  properties: {}
  roles: string[]
  permissions: string[]
  emailVerified: boolean
  isEmailVerified: boolean
  isDefaultAvatar: boolean
  isOnline: boolean
  isAdmin: boolean
  isGlobalAdmin: boolean
  isForbidden: boolean
  isDeleted: boolean
}

const isUserInfo = (subject: string | number | UserInfo): subject is UserInfo => {
  return typeof subject === 'object'
}

export class User {
  avater?: string
  subject: string | number
  nick: string
  roles?: Array<string>
  permissions?: Array<string>

  constructor(
    userInfo: string | number | UserInfo,
    roles?: Array<string>,
    permissions?: Array<string>
  ) {
    if (isUserInfo(userInfo)) {
      this.subject = userInfo.id
      this.nick = userInfo.displayName
      this.avater = userInfo.avatar
      this.roles = userInfo.roles
      this.permissions = userInfo.permissions
    } else {
      this.subject = userInfo
      this.nick = String(userInfo)
    }
    this.roles = roles
    this.permissions = permissions
  }

  getSubject(): string | number {
    return this.subject
  }

  getAvater(): string | undefined {
    return this.avater
  }

  getNick(): string {
    return this.nick
  }

  setRoles(...roles: string[]) {
    this.roles = roles
  }

  getRoles(): undefined | Array<string> {
    return this.roles
  }

  setPermissions(...permissions: string[]) {
    this.permissions = permissions
  }

  getPermissions(): undefined | Array<string> {
    return this.permissions
  }
}

const ctx: Ref<User | null> = useSessionStorage('auth', null, {
  serializer: {
    read: (v: any) => (v || v !== 'null' ? new User(JSON.parse(v)) : null),
    write: (v: any) => (v ? JSON.stringify(v) : 'null')
  }
})

const isSuperAdmin = ref((user: User) => {
  return user.getSubject() === '1' || user.getSubject() === 1
})

export const defineSuperAdmin = (fn: (u: User) => boolean) => {
  isSuperAdmin.value = fn
}

export const login = (user: User) => {
  ctx.value = user
}

export const logout = () => {
  return (ctx.value = null)
}

export const setRoles = (...roles: string[]) => {
  if (ctx.value != null) {
    ctx.value.roles = roles
  }
}

export const setPermissions = (...permissions: string[]) => {
  if (ctx.value != null) {
    ctx.value.permissions = permissions
  }
}

export const isLogin = (): Boolean => {
  return ctx.value != null
}

export const hasRoles = (...roles: string[]) => {
  if (ctx.value == null) {
    return false
  }
  if (isSuperAdmin.value(ctx.value)) {
    return true
  }
  if (!ctx.value.roles) {
    return false
  }
  for (const role of roles) {
    if (ctx.value.roles.includes(role)) {
      return true
    }
  }
  return false
}

export const hasPermissions = (...permissions: string[]) => {
  if (ctx.value == null) {
    return false
  }
  if (isSuperAdmin.value(ctx.value)) {
    return true
  }
  if (!ctx.value.permissions) {
    return false
  }
  for (const p of permissions) {
    if (ctx.value.permissions.includes(p)) {
      return true
    }
  }
  return false
}

export const useAuth = () => {
  return {
    user: ctx,
    defineSuperAdmin,
    isLogin,
    hasRoles,
    hasPermissions,
    login,
    logout,
    setRoles,
    setPermissions
  }
}
