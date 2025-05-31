N = 7
stack = []
input_data = input()

def comb(n, k):
    res = 1
    for i in range(n - k + 1, n + 1):
        res *= i
    for i in range(1, k + 1):
        res //= i
    return res

def pair(A, B):
    result = [0] * N
    for idx in range(N):
        result[idx] = sum(A[i] * B[idx - i] for i in range(idx + 1))
    return result

def seq(A):
    result = [0] * N
    result[0] = 1
    for idx in range(1, N):
        result[idx] = sum(A[i] * result[idx - i] for i in range(1, idx + 1))
    return result

def mul_set(A):
    temp = [[0] * N for _ in range(N)]
    for i in range(N):
        temp[0][i] = 1
    for i in range(1, N):
        for j in range(1, N):
            if A[j] > 0:
                for k in range(i // j + 1):
                    temp[i][j] += comb(A[j] + k - 1, k) * temp[i - k * j][j - 1]
            else:
                temp[i][j] = temp[i][j - 1]
    return [temp[i][i] for i in range(N)]

for idx in range(len(input_data) - 1, -1, -1):
    if input_data[idx] == 'B':
        stack.append([0, 1, 0, 0, 0, 0, 0])
    elif input_data[idx] == 'L':
        temp = stack.pop()
        stack.append(seq(temp))
    elif input_data[idx] == 'S':
        temp = stack.pop()
        stack.append(mul_set(temp))
    elif input_data[idx] == 'P':
        temp1 = stack.pop()
        temp2 = stack.pop()
        stack.append(pair(temp1, temp2))

result = stack[-1]
print(*result)