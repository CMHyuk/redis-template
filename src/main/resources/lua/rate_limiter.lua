local key = KEYS[1]
local bucketCapacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])
local intervalInSeconds = tonumber(ARGV[4])

-- Redis에서 명시적으로 키 가져오기
local tokens = tonumber(redis.call('HGET', key, 'tokens')) or bucketCapacity

local lastRefillTime = tonumber(redis.call('HGET', key, 'lastRefillTime')) or currentTime

-- 지난 시간 동안 생성된 토큰 계산
local elapsedTime = currentTime - lastRefillTime
local newTokens = math.min(bucketCapacity, tokens + (elapsedTime * refillRate / intervalInSeconds))

-- 토큰 부족 여부 판단
if newTokens < 1 then
    return 0
else
    redis.call('HSET', key, 'tokens', newTokens - 1, 'lastRefillTime', currentTime)
    redis.call('EXPIRE', key, 60) -- TTL 1분 설정
    return 1
end
