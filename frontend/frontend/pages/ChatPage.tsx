import type {OrchestrationRequest, OrchestrationResponse} from "../types/chat"

"use client"

import type React from "react"
import { useState, useRef, useEffect } from "react"
import { Send, Loader2, User, Bot } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Textarea } from "@/components/ui/textarea"
import { cn } from "@/lib/utils"

type Message = {
  id: string
  content: string
  sender: "user" | "ai"
  timestamp: Date
}

export default function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      content: "Hello! How can I assist you today?",
      sender: "ai",
      timestamp: new Date(),
    },
  ])
  const [input, setInput] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const textareaRef = useRef<HTMLTextAreaElement>(null)

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
  }

  const handleSendMessage = async () => {
    if (!input.trim()) return;
    const userMessage: Message = {
      id: Date.now().toString(),
      content: input,
      sender: 'user',
      timestamp: new Date(),
    };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);
  
    try {
      const body: OrchestrationRequest = {
        rawText: input,
        inputType: 'text',        // 필요하다면 동적으로 변경
        fileName: '',             // 파일 업로드 기능이 있다면 해당 값으로
        metadata: {},             // 추가 메타정보가 있으면 여기에 넣기
      };
  
      const response = await fetch('/api/orchestrator/ask', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
      });
      if (!response.ok) {
        throw new Error(`Request failed: ${response.status}`);
      }

      const res = await response.json() as OrchestrationResponse
      const narrative = res.data.narrative;
  
      // 5️⃣ AI 메시지로 변환하여 추가
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        content: narrative,
        sender: 'ai',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, aiMessage]);
  
    } catch (err) {
      console.error('Error sending message:', err);
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        content: '죄송합니다. 요청 처리 중 오류가 발생했습니다.',
        sender: 'ai',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, errorMessage]);
  
    } finally {
      setIsLoading(false);
      textareaRef.current?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault()
      handleSendMessage()
    }
  }

  return (
    <div className="flex flex-col h-[calc(100vh-8rem)] max-w-3xl mx-auto">
      <div className="flex-1 overflow-y-auto py-4 space-y-6">
        {messages.map((message) => (
          <div
            key={message.id}
            className={cn(
              "px-4 md:px-6 py-2 max-w-3xl mx-auto",
              message.sender === "user" ? "border-l-0" : "border-l-0",
            )}
          >
            <div className="flex items-start gap-4 text-sm">
              <div className="flex-shrink-0 mt-1">
                {message.sender === "user" ? (
                  <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center">
                    <User className="h-4 w-4 text-secondary-foreground" />
                  </div>
                ) : (
                  <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center">
                    <Bot className="h-4 w-4 text-primary-foreground" />
                  </div>
                )}
              </div>
              <div className="flex-1 space-y-2">
                <div className="font-medium">{message.sender === "user" ? "You" : "AI Assistant"}</div>
                <div className="prose dark:prose-invert prose-sm max-w-none">
                  <p className="whitespace-pre-wrap">{message.content}</p>
                </div>
              </div>
            </div>
          </div>
        ))}
        {isLoading && (
          <div className="px-4 md:px-6 py-2 max-w-3xl mx-auto">
            <div className="flex items-start gap-4 text-sm">
              <div className="flex-shrink-0 mt-1">
                <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center">
                  <Bot className="h-4 w-4 text-primary-foreground" />
                </div>
              </div>
              <div className="flex-1 space-y-2">
                <div className="font-medium">AI Assistant</div>
                <div className="prose dark:prose-invert prose-sm max-w-none">
                  <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                </div>
              </div>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      <div className="border-t border-border p-4">
        <div className="relative">
          <Textarea
            ref={textareaRef}
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Message AI Assistant..."
            className="min-h-[60px] resize-none pr-12 py-3 rounded-lg border-input focus:border-ring focus:ring-ring"
            disabled={isLoading}
            rows={1}
          />
          <Button
            onClick={handleSendMessage}
            disabled={isLoading || !input.trim()}
            className="absolute right-2 bottom-2 h-8 w-8 p-0 bg-primary hover:bg-primary/90 rounded-md"
            aria-label="Send message"
          >
            <Send className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  )
}
