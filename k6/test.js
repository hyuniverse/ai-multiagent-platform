import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 }, // 30초간 사용자 10명
    { duration: '1m', target: 50 },  // 1분간 사용자 50명
    { duration: '2m', target: 100 }, // 2분간 사용자 100명
  ],
};

export default function () {
  const url = 'http://localhost:8080/api/v1/orchestrator/ask';
  const payload = JSON.stringify({
    rawText: 'Summarize this article and detect sentiment.',
    inputType: 'text',
  });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(url, payload, params);

  check(res, { 'status was 200': (r) => r.status == 200 });
  sleep(1);
}