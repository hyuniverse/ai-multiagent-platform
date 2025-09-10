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

async function* streamSse(url: string, body: OrchestrationRequest, signal: AbortSignal) {
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'text/event-stream'
        },
        body: JSON.stringify(body),
        signal: signal,
    });

    if (!response.ok || !response.body) {
        throw new Error(`Streaming request failed: ${response.status}`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buffer = '';

    while (true) {
        const { value, done } = await reader.read();
        if (done) {
            break;
        }
        buffer += decoder.decode(value, { stream: true });

        let boundaryIndex;
        // SSE 이벤트 경계("\n\n")를 기준으로 반복 처리
        while ((boundaryIndex = buffer.indexOf('\n\n')) >= 0) {
            const eventBlock = buffer.substring(0, boundaryIndex);
            buffer = buffer.substring(boundaryIndex + 2);

            const dataLines = eventBlock
                .split('\n')
                .filter(line => line.startsWith('data:'))
                .map(line => line.slice(5));

            if (dataLines.length === 0) continue;

            const data = dataLines.join('\n');

            try {
              console.debug('[SSE DEBUG] eventBlock=', JSON.stringify(eventBlock), ' data=', JSON.stringify(data));
            } catch { /* no-op */ }

            if (data === '[DONE]') {
                return; // 스트림 정상 종료
            }
            yield data;
        }
    }
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
  const [abortController, setAbortController] = useState<AbortController | null>(null)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const textareaRef = useRef<HTMLTextAreaElement>(null)

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  useEffect(() => {
    return () => abortController?.abort()
  }, [abortController])

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

      const aiMessageId = (Date.now() + 1).toString();

      const initialAiMessage: Message = {
        id: aiMessageId,
        content: "",
        sender: 'ai',
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, userMessage, initialAiMessage]);
      setInput('');
      setIsLoading(true);

      const controller = new AbortController();
      setAbortController(controller);

      try {
          const body: OrchestrationRequest = {
              rawText: input,
              inputType: 'text',
              fileName: '',
              metadata: {},
          };
          
          // 스트림 처리
          for await (const chunk of streamSse('/api/orchestrator/ask', body, controller.signal)) {
              setMessages(prev =>
                  prev.map(m =>
                      m.id === aiMessageId ? { ...m, content: m.content + chunk } : m
                  )
              );
          }

      } catch (err) {
          // AbortController에 의해 취소된 경우는 에러 메시지를 표시하지 않음
          if (err instanceof DOMException && err.name === 'AbortError') {
              console.log('Stream aborted by user.');
          } else {
              console.error('Error sending / streaming message:', err);
              setMessages(prev => prev.map(m => m.id === aiMessageId ? { ...m, content: '죄송합니다. 요청 처리 중 오류가 발생했습니다.' } : m));
          }
      } finally {
          setIsLoading(false);
          setAbortController(null);
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
