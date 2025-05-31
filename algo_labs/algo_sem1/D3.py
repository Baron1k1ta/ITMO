def push(stack, n):
    global size
    stack[size] = n
    size+=1

def pop(stack):
    global size
    size = size-1
    return stack[size]
size = 0
str = input().split(" ")
ar = []
for i in range(1, len(str)):
    ar.append(int(str[i]))
stack = [0 for i in range(1000000)]
max_area = 0
ar.append(0)
for i in range(len(ar)):
    while size > 0 and ar[stack[size-1]] > ar[i]:
        height = ar[pop(stack)]
        if size == 0:
            width = i
        else:
            width = i - stack[size-1] - 1
        max_area = max(max_area, height * width)
    push(stack, i)
print(max_area)