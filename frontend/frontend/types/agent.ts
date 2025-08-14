export interface AgentRegisterRequest {
  name: string
  type: string
  protocol: "rest" | "grpc" | "mcp" | "leg"
  endpoint: string
  hasMemory: boolean
  memoryType?: string
  inputTypes: string[]
  outputTypes: string[]
  description?: string
}

export interface AgentDetailResponse {
  uuid: string
  name: string
  type: string
  protocol: "rest" | "grpc" | "mcp" | "leg"
  endpoint: string
  hasMemory: boolean
  memoryType?: string
  inputTypes: string[]
  outputTypes: string[]
  description?: string
  status: "active" | "inactive" | "failed"
  reachable: boolean
  requestCount: number
}
