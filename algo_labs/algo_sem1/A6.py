n = int(input())
field = input()
squers = []
        for i in range(len(field)):
        if field[i] == '.':
        squers.append(0)
elif field[i] == 'w':
        squers.append(-100_000)
    else:
            squers.append(1)

dp = [0 for i in range(n)]
dp[0] = squers[0]
dp[1] = squers[0]+squers[1]
        for i in range(2,n):
dp[i] = -100_000_000
dp[i] = max(dp[i], dp[i-1])
    if (i >=3):
dp[i] = max(dp[i], dp[i - 3])
    if (i >=5):
dp[i] = max(dp[i], dp[i - 5])
dp[i]+=squers[i]
        if (dp[n-1] <0):
print(-1)
else:
print(dp[n-1])