"use client"

import React from "react"

import { useState, useEffect } from "react"
import { Plus, Loader2, Pencil, Trash2, ChevronDown, ChevronUp, ExternalLink } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogFooter } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card"
import { Checkbox } from "@/components/ui/checkbox"
import { Textarea } from "@/components/ui/textarea"
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/frontend/components/ui/collapsible"
import { useToast } from "@/frontend/hooks/use-toast"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import type { AgentDetailResponse, AgentRegisterRequest } from "@/frontend/types/agent"
import { fetchAgents, createAgent, updateAgent, deleteAgent } from "@/frontend/api/agents"
import { Toaster } from "@/frontend/components/ui/toaster"

export default function AgentManagementPage() {
  const { toast } = useToast()
  const [agents, setAgents] = useState<AgentDetailResponse[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false)
  const [expandedAgentId, setExpandedAgentId] = useState<string | null>(null)
  const [newAgent, setNewAgent] = useState<AgentRegisterRequest>({
    name: "",
    type: "",
    protocol: "rest",
    endpoint: "",
    hasMemory: false,
    inputTypes: [],
    outputTypes: [],
    description: "",
  })
  const [selectedAgent, setSelectedAgent] = useState<AgentDetailResponse | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [inputTypesString, setInputTypesString] = useState("")
  const [outputTypesString, setOutputTypesString] = useState("")
  const [editInputTypesString, setEditInputTypesString] = useState("")
  const [editOutputTypesString, setEditOutputTypesString] = useState("")

  useEffect(() => {
    loadAgents()
  }, [])

  const loadAgents = async () => {
    setIsLoading(true)
    try {
      const data = await fetchAgents()
      setAgents(data)
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to fetch agents",
      })
    } finally {
      setIsLoading(false)
    }
  }

  const handleCreateAgent = async () => {
    if (!newAgent.name.trim() || !newAgent.type.trim() || !newAgent.endpoint.trim()) return

    // Parse comma-separated strings to arrays
    const agentToCreate: AgentRegisterRequest = {
      ...newAgent,
      inputTypes: inputTypesString
        .split(",")
        .map((type) => type.trim())
        .filter(Boolean),
      outputTypes: outputTypesString
        .split(",")
        .map((type) => type.trim())
        .filter(Boolean),
    }

    setIsSubmitting(true)
    try {
      await createAgent(agentToCreate)
      await loadAgents()
      resetNewAgentForm()
      setIsCreateDialogOpen(false)
      toast({
        title: "Success",
        description: "Agent created successfully",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to create agent",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleUpdateAgent = async () => {
    if (!selectedAgent || !selectedAgent.name.trim() || !selectedAgent.type.trim() || !selectedAgent.endpoint.trim())
      return

    // Parse comma-separated strings to arrays
    const agentToUpdate: AgentRegisterRequest = {
      name: selectedAgent.name,
      type: selectedAgent.type,
      protocol: selectedAgent.protocol,
      endpoint: selectedAgent.endpoint,
      hasMemory: selectedAgent.hasMemory,
      memoryType: selectedAgent.memoryType,
      inputTypes: editInputTypesString
        .split(",")
        .map((type) => type.trim())
        .filter(Boolean),
      outputTypes: editOutputTypesString
        .split(",")
        .map((type) => type.trim())
        .filter(Boolean),
      description: selectedAgent.description,
    }

    setIsSubmitting(true)
    try {
      await updateAgent(selectedAgent.uuid, agentToUpdate)
      await loadAgents()
      setIsEditDialogOpen(false)
      toast({
        title: "Success",
        description: "Agent updated successfully",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to update agent",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDeleteAgent = async () => {
    if (!selectedAgent) return

    setIsSubmitting(true)
    try {
      await deleteAgent(selectedAgent.uuid)
      await loadAgents()
      setIsDeleteDialogOpen(false)
      toast({
        title: "Success",
        description: "Agent deleted successfully",
      })
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Error",
        description: error instanceof Error ? error.message : "Failed to delete agent",
      })
    } finally {
      setIsSubmitting(false)
    }
  }

  const openEditDialog = (agent: AgentDetailResponse) => {
    setSelectedAgent(agent)
    setEditInputTypesString(agent.inputTypes.join(", "))
    setEditOutputTypesString(agent.outputTypes.join(", "))
    setIsEditDialogOpen(true)
  }

  const openDeleteDialog = (agent: AgentDetailResponse) => {
    setSelectedAgent(agent)
    setIsDeleteDialogOpen(true)
  }

  const resetNewAgentForm = () => {
    setNewAgent({
      name: "",
      type: "",
      protocol: "rest",
      endpoint: "",
      hasMemory: false,
      inputTypes: [],
      outputTypes: [],
      description: "",
    })
    setInputTypesString("")
    setOutputTypesString("")
  }

  const toggleAgentDetails = (agentId: string) => {
    setExpandedAgentId(expandedAgentId === agentId ? null : agentId)
  }

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case "active":
        return "bg-primary hover:bg-primary/90"
      case "inactive":
        return "text-muted-foreground"
      case "failed":
        return "bg-destructive hover:bg-destructive/90"
      default:
        return "text-muted-foreground"
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Agent Management</h1>
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button className="bg-primary hover:bg-primary/90">
              <Plus className="mr-2 h-4 w-4" /> Add Agent
            </Button>
          </DialogTrigger>
          <DialogContent className="max-h-[90vh] overflow-y-auto">
            <DialogHeader>
              <DialogTitle>Create New Agent</DialogTitle>
            </DialogHeader>
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <Label htmlFor="name">Name *</Label>
                <Input
                  id="name"
                  value={newAgent.name}
                  onChange={(e) => setNewAgent({ ...newAgent, name: e.target.value })}
                  placeholder="Enter agent name"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="type">Type *</Label>
                <Input
                  id="type"
                  value={newAgent.type}
                  onChange={(e) => setNewAgent({ ...newAgent, type: e.target.value })}
                  placeholder="e.g., REACT"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="protocol">Protocol *</Label>
                <Select
                  value={newAgent.protocol}
                  onValueChange={(value) =>
                    setNewAgent({
                      ...newAgent,
                      protocol: value as "rest",
                    })
                  }
                >
                  <SelectTrigger id="protocol">
                    <SelectValue placeholder="Select protocol" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="rest">REST</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label htmlFor="endpoint">Endpoint *</Label>
                <Input
                  id="health check"
                  value={newAgent.endpoint}
                  onChange={(e) => setNewAgent({ ...newAgent, endpoint: e.target.value })}
                  placeholder="e.g., http://localhost:8081/execute"
                />
              </div>
              {/* <div className="space-y-2">
                <Label htmlFor="endpoint">Endpoint *</Label>
                <Input
                  id="endpoint"
                  value={newAgent.endpoint}
                  onChange={(e) => setNewAgent({ ...newAgent, endpoint: e.target.value })}
                  placeholder="e.g., http://localhost:8081/execute"
                />
              </div> */}
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="hasMemory"
                  checked={newAgent.hasMemory}
                  onCheckedChange={(checked) => setNewAgent({ ...newAgent, hasMemory: checked as boolean })}
                />
                <Label htmlFor="hasMemory">Has Memory</Label>
              </div>
              {newAgent.hasMemory && (
                <div className="space-y-2">
                  <Label htmlFor="memoryType">Memory Type</Label>
                  <Input
                    id="memoryType"
                    value={newAgent.memoryType || ""}
                    onChange={(e) => setNewAgent({ ...newAgent, memoryType: e.target.value })}
                    placeholder="e.g., redis"
                  />
                </div>
              )}
              <div className="space-y-2">
                <Label htmlFor="inputTypes">Input Types * (comma-separated)</Label>
                <Input
                  id="inputTypes"
                  value={inputTypesString}
                  onChange={(e) => setInputTypesString(e.target.value)}
                  placeholder="e.g., TEXT, IMAGE"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="outputTypes">Output Types * (comma-separated)</Label>
                <Input
                  id="outputTypes"
                  value={outputTypesString}
                  onChange={(e) => setOutputTypesString(e.target.value)}
                  placeholder="e.g., TEXT, JSON"
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="description">Description</Label>
                <Textarea
                  id="description"
                  value={newAgent.description || ""}
                  onChange={(e) => setNewAgent({ ...newAgent, description: e.target.value })}
                  placeholder="Enter agent description"
                  rows={3}
                />
              </div>
            </div>
            <DialogFooter>
              <Button
                onClick={handleCreateAgent}
                disabled={isSubmitting || !newAgent.name.trim() || !newAgent.type.trim() || !newAgent.endpoint.trim()}
                className="w-full bg-primary hover:bg-primary/90"
              >
                {isSubmitting ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
                Create Agent
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className="flex justify-center items-center h-64">
          <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        </div>
      ) : (
        <>
          {/* Desktop view: Table */}
          <div className="hidden md:block">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Name</TableHead>
                  <TableHead>Type</TableHead>
                  <TableHead>Protocol</TableHead>
                  <TableHead>Endpoint</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {agents.map((agent) => (
                  <React.Fragment key={agent.uuid}>
                    <TableRow
                      className="cursor-pointer hover:bg-muted/50"
                      onClick={() => toggleAgentDetails(agent.uuid)}
                    >
                      <TableCell className="font-medium">{agent.name}</TableCell>
                      <TableCell>{agent.type}</TableCell>
                      <TableCell className="uppercase">{agent.protocol}</TableCell>
                      <TableCell className="truncate max-w-[200px]">
                        <div className="flex items-center">
                          <span className="truncate">{agent.endpoint}</span>
                          <a
                            href={agent.endpoint}
                            target="_blank"
                            rel="noopener noreferrer"
                            onClick={(e) => e.stopPropagation()}
                            className="ml-1 text-muted-foreground hover:text-foreground"
                          >
                            <ExternalLink className="h-3 w-3" />
                          </a>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge
                          variant={agent.status === "active" ? "default" : "outline"}
                          className={getStatusBadgeClass(agent.status)}
                        >
                          {agent.status}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center space-x-2">
                          <Button
                            variant="outline"
                            size="sm"
                            className="text-xs"
                            onClick={(e) => {
                              e.stopPropagation()
                              openEditDialog(agent)
                            }}
                          >
                            <Pencil className="h-3 w-3 mr-1" /> Edit
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            className="text-xs text-destructive"
                            onClick={(e) => {
                              e.stopPropagation()
                              openDeleteDialog(agent)
                            }}
                          >
                            <Trash2 className="h-3 w-3 mr-1" /> Delete
                          </Button>
                          {expandedAgentId === agent.uuid ? (
                            <ChevronUp className="h-4 w-4 text-muted-foreground" />
                          ) : (
                            <ChevronDown className="h-4 w-4 text-muted-foreground" />
                          )}
                        </div>
                      </TableCell>
                    </TableRow>
                    {expandedAgentId === agent.uuid && (
                      <TableRow>
                        <TableCell colSpan={7} className="p-0">
                          <div className="bg-muted/30 p-4 rounded-md m-2">
                            <h3 className="text-sm font-medium mb-2">Agent Details</h3>
                            <div className="grid grid-cols-2 gap-4">
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">UUID</p>
                                <p className="text-sm font-mono">{agent.uuid}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Name</p>
                                <p className="text-sm">{agent.name}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Type</p>
                                <p className="text-sm">{agent.type}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Protocol</p>
                                <p className="text-sm uppercase">{agent.protocol}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Endpoint</p>
                                <p className="text-sm break-all">{agent.endpoint}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Status</p>
                                <Badge
                                  variant={agent.status === "active" ? "default" : "outline"}
                                  className={getStatusBadgeClass(agent.status)}
                                >
                                  {agent.status}
                                </Badge>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Has Memory</p>
                                <p className="text-sm">{agent.hasMemory ? "Yes" : "No"}</p>
                              </div>
                              {agent.hasMemory && (
                                <div>
                                  <p className="text-xs text-muted-foreground mb-1">Memory Type</p>
                                  <p className="text-sm">{agent.memoryType || "N/A"}</p>
                                </div>
                              )}
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Input Types</p>
                                <div className="flex flex-wrap gap-1">
                                  {agent.inputTypes.map((type, index) => (
                                    <Badge key={index} variant="outline" className="text-xs">
                                      {type}
                                    </Badge>
                                  ))}
                                </div>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Output Types</p>
                                <div className="flex flex-wrap gap-1">
                                  {agent.outputTypes.map((type, index) => (
                                    <Badge key={index} variant="outline" className="text-xs">
                                      {type}
                                    </Badge>
                                  ))}
                                </div>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Reachable</p>
                                <p className="text-sm">{agent.reachable ? "Yes" : "No"}</p>
                              </div>
                              <div>
                                <p className="text-xs text-muted-foreground mb-1">Request Count</p>
                                <p className="text-sm">{agent.requestCount}</p>
                              </div>
                              {agent.description && (
                                <div className="col-span-2">
                                  <p className="text-xs text-muted-foreground mb-1">Description</p>
                                  <p className="text-sm">{agent.description}</p>
                                </div>
                              )}
                            </div>
                          </div>
                        </TableCell>
                      </TableRow>
                    )}
                  </React.Fragment>
                ))}
              </TableBody>
            </Table>
          </div>

          {/* Mobile view: Cards */}
          <div className="grid grid-cols-1 gap-4 md:hidden">
            {agents.map((agent) => (
              <Collapsible
                key={agent.uuid}
                open={expandedAgentId === agent.uuid}
                onOpenChange={() => toggleAgentDetails(agent.uuid)}
              >
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-lg flex justify-between items-center">
                      <span>{agent.name}</span>
                      <CollapsibleTrigger asChild>
                        <Button variant="ghost" size="sm" className="h-8 w-8 p-0">
                          {expandedAgentId === agent.uuid ? (
                            <ChevronUp className="h-4 w-4" />
                          ) : (
                            <ChevronDown className="h-4 w-4" />
                          )}
                        </Button>
                      </CollapsibleTrigger>
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="space-y-2">
                      <div className="flex justify-between">
                        <span className="text-sm text-muted-foreground">UUID:</span>
                        <span className="font-mono text-xs">{agent.uuid}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-sm text-muted-foreground">Type:</span>
                        <span className="text-sm">{agent.type}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-sm text-muted-foreground">Protocol:</span>
                        <span className="text-sm uppercase">{agent.protocol}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-sm text-muted-foreground">Status:</span>
                        <Badge
                          variant={agent.status === "active" ? "default" : "outline"}
                          className={getStatusBadgeClass(agent.status)}
                        >
                          {agent.status}
                        </Badge>
                      </div>
                    </div>

                    <CollapsibleContent className="mt-4 space-y-3 border-t pt-3">
                      <div className="space-y-2">
                        <div>
                          <span className="text-sm text-muted-foreground">Endpoint:</span>
                          <p className="text-sm break-all mt-1">{agent.endpoint}</p>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-sm text-muted-foreground">Has Memory:</span>
                          <span className="text-sm">{agent.hasMemory ? "Yes" : "No"}</span>
                        </div>
                        {agent.hasMemory && (
                          <div className="flex justify-between">
                            <span className="text-sm text-muted-foreground">Memory Type:</span>
                            <span className="text-sm">{agent.memoryType || "N/A"}</span>
                          </div>
                        )}
                        <div>
                          <span className="text-sm text-muted-foreground">Input Types:</span>
                          <div className="flex flex-wrap gap-1 mt-1">
                            {agent.inputTypes.map((type, index) => (
                              <Badge key={index} variant="outline" className="text-xs">
                                {type}
                              </Badge>
                            ))}
                          </div>
                        </div>
                        <div>
                          <span className="text-sm text-muted-foreground">Output Types:</span>
                          <div className="flex flex-wrap gap-1 mt-1">
                            {agent.outputTypes.map((type, index) => (
                              <Badge key={index} variant="outline" className="text-xs">
                                {type}
                              </Badge>
                            ))}
                          </div>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-sm text-muted-foreground">Reachable:</span>
                          <span className="text-sm">{agent.reachable ? "Yes" : "No"}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-sm text-muted-foreground">Request Count:</span>
                          <span className="text-sm">{agent.requestCount}</span>
                        </div>
                        {agent.description && (
                          <div>
                            <span className="text-sm text-muted-foreground">Description:</span>
                            <p className="text-sm mt-1">{agent.description}</p>
                          </div>
                        )}
                      </div>
                    </CollapsibleContent>
                  </CardContent>
                  <CardFooter className="flex space-x-2 pt-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1 text-xs"
                      onClick={() => openEditDialog(agent)}
                    >
                      <Pencil className="h-3 w-3 mr-1" /> Edit
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1 text-xs text-destructive"
                      onClick={() => openDeleteDialog(agent)}
                    >
                      <Trash2 className="h-3 w-3 mr-1" /> Delete
                    </Button>
                  </CardFooter>
                </Card>
              </Collapsible>
            ))}
          </div>
        </>
      )}

      {/* Edit Agent Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Edit Agent</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="edit-name">Name *</Label>
              <Input
                id="edit-name"
                value={selectedAgent?.name || ""}
                onChange={(e) => setSelectedAgent(selectedAgent ? { ...selectedAgent, name: e.target.value } : null)}
                placeholder="Enter agent name"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-type">Type *</Label>
              <Input
                id="edit-type"
                value={selectedAgent?.type || ""}
                onChange={(e) => setSelectedAgent(selectedAgent ? { ...selectedAgent, type: e.target.value } : null)}
                placeholder="e.g., REACT"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-protocol">Protocol *</Label>
              <Select
                value={selectedAgent?.protocol || "rest"}
                onValueChange={(value) =>
                  setSelectedAgent(
                    selectedAgent ? { ...selectedAgent, protocol: value as "rest" | "grpc" | "mcp" | "leg" } : null,
                  )
                }
              >
                <SelectTrigger id="edit-protocol">
                  <SelectValue placeholder="Select protocol" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="rest">REST</SelectItem>
                  <SelectItem value="grpc">gRPC</SelectItem>
                  <SelectItem value="mcp">MCP</SelectItem>
                  <SelectItem value="leg">LEG</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-endpoint">Endpoint *</Label>
              <Input
                id="edit-endpoint"
                value={selectedAgent?.endpoint || ""}
                onChange={(e) =>
                  setSelectedAgent(selectedAgent ? { ...selectedAgent, endpoint: e.target.value } : null)
                }
                placeholder="e.g., http://localhost:8081/execute"
              />
            </div>
            <div className="flex items-center space-x-2">
              <Checkbox
                id="edit-hasMemory"
                checked={selectedAgent?.hasMemory || false}
                onCheckedChange={(checked) =>
                  setSelectedAgent(selectedAgent ? { ...selectedAgent, hasMemory: checked as boolean } : null)
                }
              />
              <Label htmlFor="edit-hasMemory">Has Memory</Label>
            </div>
            {selectedAgent?.hasMemory && (
              <div className="space-y-2">
                <Label htmlFor="edit-memoryType">Memory Type</Label>
                <Input
                  id="edit-memoryType"
                  value={selectedAgent?.memoryType || ""}
                  onChange={(e) =>
                    setSelectedAgent(selectedAgent ? { ...selectedAgent, memoryType: e.target.value } : null)
                  }
                  placeholder="e.g., redis"
                />
              </div>
            )}
            <div className="space-y-2">
              <Label htmlFor="edit-inputTypes">Input Types * (comma-separated)</Label>
              <Input
                id="edit-inputTypes"
                value={editInputTypesString}
                onChange={(e) => setEditInputTypesString(e.target.value)}
                placeholder="e.g., TEXT, IMAGE"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-outputTypes">Output Types * (comma-separated)</Label>
              <Input
                id="edit-outputTypes"
                value={editOutputTypesString}
                onChange={(e) => setEditOutputTypesString(e.target.value)}
                placeholder="e.g., TEXT, JSON"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="edit-description">Description</Label>
              <Textarea
                id="edit-description"
                value={selectedAgent?.description || ""}
                onChange={(e) =>
                  setSelectedAgent(selectedAgent ? { ...selectedAgent, description: e.target.value } : null)
                }
                placeholder="Enter agent description"
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              onClick={handleUpdateAgent}
              disabled={
                isSubmitting ||
                !selectedAgent?.name.trim() ||
                !selectedAgent?.type.trim() ||
                !selectedAgent?.endpoint.trim()
              }
              className="w-full bg-primary hover:bg-primary/90"
            >
              {isSubmitting ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
              Update Agent
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={isDeleteDialogOpen} onOpenChange={setIsDeleteDialogOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This will permanently delete the agent "{selectedAgent?.name}". This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isSubmitting}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteAgent}
              disabled={isSubmitting}
              className="bg-destructive hover:bg-destructive/90 text-destructive-foreground"
            >
              {isSubmitting ? <Loader2 className="h-4 w-4 animate-spin mr-2" /> : null}
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <Toaster />
    </div>
  )
}
