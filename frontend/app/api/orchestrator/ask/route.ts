export const runtime = 'nodejs';
export const dynamic = 'force-dynamic';

// Pass-through streaming proxy for SSE to avoid buffering in rewrites/proxies
export async function POST(req: Request) {
  const API_ORIGIN = process.env.API_ORIGIN || 'http://localhost:8080';

  // Forward the request body as a stream to the backend
  const upstream = await fetch(`${API_ORIGIN}/api/v1/orchestrator/ask`, {
    method: 'POST',
    headers: {
      'Content-Type': req.headers.get('content-type') || 'application/json',
      'Accept': 'text/event-stream',
    },
    // In Node.js runtime, streaming request bodies require duplex: 'half'
    // @ts-expect-error - duplex is a Node.js extension to the Fetch API
    duplex: 'half',
    body: req.body ?? null,
    // Do not cache
    cache: 'no-store',
  });

  if (!upstream.ok || !upstream.body) {
    const text = await upstream.text().catch(() => 'Upstream error');
    return new Response(text, { status: upstream.status });
  }

  // Pipe upstream response body to the client without buffering
  const { readable, writable } = new TransformStream();
  upstream.body
    .pipeTo(writable)
    .catch(() => {/* ignore downstream aborts */});

  return new Response(readable, {
    status: 200,
    headers: {
      'Content-Type': 'text/event-stream; charset=utf-8',
      'Cache-Control': 'no-cache, no-transform',
      'Connection': 'keep-alive',
      // Some proxies (nginx) respect this to disable buffering
      // 'X-Accel-Buffering': 'no',
    },
  });
}
