n, k = map(int, input().split())
numbers = []
maxim = 0
summa = 0
for i in range(n):
    numbers.append(int(input()))
    maxim = max(maxim, numbers[i])
    summa += numbers[i]
if summa / k < 1:
    print(0)
else:
    left = 0
    right = maxim
    mid = (left + right) // 2
    while left <= right:
        mid = (left + right) // 2
        count = 0
        for i in range(n):
            count += numbers[i] // mid
        if count < k:
            right = mid - 1
        else:
            left = mid + 1
    print(right)
 