export interface OrchestrationRequest {
    rawText: string
    inputType: string
    fileName: string
    metadata: Record<string, string>
  }

export interface OrchestrationResponse {
    code: number
    status: string
    message: string
    data: {
        narrative: string
    }
}