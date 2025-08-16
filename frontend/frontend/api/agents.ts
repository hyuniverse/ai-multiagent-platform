import type { AgentDetailResponse, AgentRegisterRequest } from "../types/agent"

const API_BASE_URL = "http://backend:8080"

export async function fetchAgents(): Promise<AgentDetailResponse[]> {
  try {
    const response = await fetch("/api/agents");

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
    throw new Error(`2Failed to fetch agents: ${error instanceof Error ? error.message : String(error)}`);
  }
}
export async function createAgent(agent: AgentRegisterRequest): Promise<AgentDetailResponse> {
  try {
    const response = await fetch(`/api/agents`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(agent),
    })

    const res = await response.json();

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `Failed to create agent: ${response.status}`)
    }

    // fetchAgents();

    return await res;
  } catch (error) {
    throw new Error(`Failed to create agent: ${error instanceof Error ? error.message : String(error)}`)
  }
}

export async function updateAgent(uuid: string, agent: AgentRegisterRequest): Promise<AgentDetailResponse> {
  try {
    const response = await fetch(`/api/agents/${uuid}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(agent),
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `Failed to update agent: ${response.status}`)
    }

    return await response.json()
  } catch (error) {
    throw new Error(`Failed to update agent: ${error instanceof Error ? error.message : String(error)}`)
  }
}

export async function deleteAgent(uuid: string): Promise<void> {
  try {
    const response = await fetch(`/api/agents/${uuid}`, {
      method: "DELETE",
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      throw new Error(errorData.message || `Failed to delete agent: ${response.status}`)
    }
  } catch (error) {
    throw new Error(`Failed to delete agent: ${error instanceof Error ? error.message : String(error)}`)
  }
}
