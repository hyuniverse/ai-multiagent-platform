import type {OrchestrationRequest, OrchestrationResponse} from "../types/chat"

export async function fetchOrchestrator(): Promise<OrchestrationRequest[]> {
    try {
      const response = await fetch("/api/api/orchestrator/ask");
  
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `1Failed to fetch agents: ${response.status}`);
      }
  
      const result = await response.json();
  
      if (!Array.isArray(result.data)) {
        throw new Error("API 응답 형식이 예상과 다릅니다: data가 배열이 아닙니다.");
      }
  
      return result.data;
    } catch (error) {
      throw new Error(`Failed to fetch agents: ${error instanceof Error ? error.message : String(error)}`);
    }
  }