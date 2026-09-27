"""
parse_industry_excel.py로 만든 JSON을 관리자 API로 등록한다.
이미지는 올리지 않는다 (메타데이터 텍스트만 등록). presigned URL은 받아오되 사용하지 않는다.

사용법:
  python3 register_industry_data.py industry_data.json \
    --base-url http://localhost:8080 \
    --session-cookie "SESSION=..." \
    --year-id 1 \
    [--limit 3]
"""
import argparse
import json
import sys
import time
import urllib.error
import urllib.request

REQUEST_FIELDS = [
    "code", "productName", "companyName", "price", "referenceUrl",
    "registeredAt", "productPath", "productTypeName", "weight",
    "modelName", "material", "size",
    "noiseCancelling", "codec", "extraFeatures", "controlType", "waterproof",
    "maxPlayTime", "chargeTime", "usage", "shoppingUrl", "soundOutput", "connectivity",
    "originalDetailImagePath", "originalFrontImagePath", "originalSideImagePath",
    "originalSide2ImagePath", "originalSide3ImagePath", "industryDataCategory",
]


def to_request_body(row):
    return {k: row.get(k) for k in REQUEST_FIELDS}


def post_json(url, body, cookie_header):
    data = json.dumps(body).encode("utf-8")
    req = urllib.request.Request(url, data=data, method="POST")
    req.add_header("Content-Type", "application/json")
    req.add_header("Cookie", cookie_header)
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.status, resp.read().decode("utf-8")
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode("utf-8")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("json_path")
    parser.add_argument("--base-url", required=True)
    parser.add_argument("--session-cookie", required=True, help='예: "SESSION=xxxxx"')
    parser.add_argument("--year-id", type=int, required=True)
    parser.add_argument("--limit", type=int, default=None)
    parser.add_argument("--sleep", type=float, default=0.0)
    args = parser.parse_args()

    with open(args.json_path, encoding="utf-8") as f:
        rows = json.load(f)

    if args.limit:
        rows = rows[:args.limit]

    url = f"{args.base_url}/api/v1/admin/industry/data/years/{args.year_id}/datasets"

    success = 0
    failed = []

    for i, row in enumerate(rows):
        body = to_request_body(row)
        status, text = post_json(url, body, args.session_cookie)

        if status != 200:
            print(f"[{i}] FAIL code={row.get('code')} status={status} body={text[:300]}", file=sys.stderr)
            failed.append(row.get("code"))
            continue

        success += 1
        if (i + 1) % 50 == 0:
            print(f"진행: {i + 1}/{len(rows)}", file=sys.stderr)

        if args.sleep:
            time.sleep(args.sleep)

    print(f"완료: 성공 {success} / 실패 {len(failed)}", file=sys.stderr)
    if failed:
        print(f"실패한 code 목록: {failed}", file=sys.stderr)


if __name__ == "__main__":
    main()
