import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';

const responseTimeTrend = new Trend('response_time');

export const options = {
  // 시나리오: 부하를 점진적으로 높여 시스템의 한계점을 찾습니다.
  scenarios: {
    breakpoint_test: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '1m', target: 100 },  // 1분간 100 VU까지 서서히 증가 (기존 성능 확인)
        { duration: '3m', target: 150 },  // 3분간 150 VU까지 서서히 증가 (부하 증폭)
        { duration: '1m', target: 150 },  // 1분간 150 VU 유지 (최대 부하 안정성 확인)
        { duration: '2m', target: 0 },    // 2분간 서서히 감소 (시스템 회복 확인)
      ],
    },
  },

  // 성능 목표(Thresholds): 테스트 성공/실패를 판단하는 기준
  thresholds: {
    // 95%의 요청이 800ms 안에 처리되어야 함
    'http_req_duration': ['p(95)<800'],
    // 전체 요청의 1% 미만만 실패해야 함
    'http_req_failed': ['rate<0.01'],
    // 체크(check) 성공률이 99% 이상이어야 함
    'checks': ['rate>0.99'],
  },
};

export default function () {
  const url = `${__ENV.BASE_URL || 'http://localhost:8080'}/api/v1/orchestrator/ask`;

  const payload = JSON.stringify({
    rawText: `Summarize article #${__VU}-${__ITER} and detect sentiment.`,
    inputType: 'text',
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
    tags: { name: 'askAPI' },
  };

  const res = http.post(url, payload, params);

  responseTimeTrend.add(res.timings.duration);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });

}