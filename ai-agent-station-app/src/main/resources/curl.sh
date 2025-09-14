# 替换 sk-xxx 为你的 API Key，测试接口是否返回正常响应
curl -X POST https://open.bigmodel.cn/api/paas/v4/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer c3e3912426c49a7b990280b1d2d235a3.tcgJOlShzn6ba6uk" \
  -d '{
    "model": "GLM-4.5-Flash",
    "messages": [{"role": "user", "content": "Hello"}],
    "stream": true
  }'