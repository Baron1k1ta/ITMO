n = int(input())
dp = []
last = []
string = input().split(" ")
nums = []
for i in range(n):
    dp.append(1)
    last.append(-1)
    nums.append(int(string[i]))
    for j in range(i):
        if nums[j] < nums[i]:
            if dp[i] < dp[j] +1:
                dp[i] = dp[j] + 1
                last[i] = j

maxim = -1
num = 0
for i in range(n):
    if dp[i] > maxim:
        maxim = dp[i]
        num = i
print(dp[num])
answer = [nums[num]]
while True:
    if last[num] == -1:
        break
    answer.append(nums[last[num]])
    num = last[num]

print(*answer[::-1])