def sort(numbers):
length = len(numbers)
    if length == 1:
        return numbers
        left = sort(numbers[:(length+1)//2])
right = sort(numbers[(length+1)//2:])
    return merge(left, right)

def merge(a, b):
n = len(a)
m = len(b)
count = 0
len_c = n + m + 1
        if n != 1:
count += a[n-1]
len_c -= 1
n -= 1
        if m != 1:
count += b[m-1]
len_c -= 1
m -= 1

c = [0] * len_c
        i = j = 0
    while i < n or j < m:
        if i < n and j < m:
        if a[i] <= b[j]:
c[i+j] = a[i]
i += 1
        else:
c[i+j] = b[j]
j += 1
count += n - i
        else:
                if i >= n:
c[i+j] = b[j]
j += 1
        else:
c[i+j] = a[i]
i += 1

c[n+m] = count
    return c

size = int(input())
numbers = list(map(int, input().split()))
        if size != 1:
sorted_nums = sort(numbers)
print(sorted_nums[size])
else:
print(0)