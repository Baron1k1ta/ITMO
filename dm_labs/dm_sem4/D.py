from fractions import Fraction

def parse_numbers(s):
    result = []
    num = ''
    for char in s:
        if char.isdigit() or char == '-':
            num += char
        else:
            if num:
                result.append(int(num))
                num = ''
    if num:
        result.append(int(num))
    return result

def to_fraction(x):
    return [Fraction(xi, 1) for xi in x]

def add_GF(x, y):
    max_len = max(len(x), len(y))
    x += [Fraction(0, 1)] * (max_len - len(x))
    y += [Fraction(0, 1)] * (max_len - len(y))
    return [a + b for a, b in zip(x, y)]

def multiply_GF(x, y):
    result = [Fraction(0, 1)] * (len(x) + len(y) - 1)
    for i in range(len(x)):
        for j in range(len(y)):
            result[i + j] += x[i] * y[j]
    return result

def create_FElem(p, ci, k):
    result = to_fraction([p])
    for i in range(1, k + 1):
        term = [
            Fraction(i - ci, i),
            Fraction(1, i)
        ]
        result = multiply_GF(result, term)
    return result

def create_F(r, k, p):
    result = []
    for pd, i in zip(p[:k + 1], range(k + 1)):
        elem = create_FElem(pd * Fraction(1, r ** i), i, k)
        result = add_GF(result, elem) if result else elem
    return result

r, k = parse_numbers(input())
p = [Fraction(x, 1) for x in parse_numbers(input())]

result = create_F(r, k, p)

output = ' '.join(f'{frac.numerator}/{frac.denominator}' for frac in result[:k+1])
print(output)