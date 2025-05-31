n = int(input())
coefficients = list(map(int, input().split()))
filter_coefficients = list(map(int, input().split()))

filter_values = [0] * (n + 1)
filter_values[0] = 1
for i in range(1, len(filter_values)):
    filter_values[i] = -filter_coefficients[i - 1]

result_values = [0] * n
for idx in range(n):
    calculated_value = 0
    for i in range(1, n + 1):
        if 0 <= idx - i < len(coefficients):
            calculated_value += filter_coefficients[i - 1] * coefficients[idx - i]
    result_values[idx] = coefficients[idx] - calculated_value

max_result_index = 0
for i in range(len(result_values) - 1, -1, -1):
    if result_values[i] != 0:
        max_result_index = i
        break
print(max_result_index)
for i in range(max_result_index + 1):
    print(result_values[i], end=" ")
print()

max_filter_index = 0
for i in range(len(filter_values) - 1, -1, -1):
    if filter_values[i] != 0:
        max_filter_index = i
        break
print(max_filter_index)
for i in range(max_filter_index + 1):
    print(filter_values[i], end=" ")
print()