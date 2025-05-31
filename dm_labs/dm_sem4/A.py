mod = 998244353


def add_poly(p, q):
    result = [(p[i] + q[i] if i < len(p) and i < len(q) else p[i] if i < len(p) else q[i]) % mod for i in
              range(max(len(p), len(q)))]
    return result


def mul_poly(p, q):
    new_len = len(p) + len(q) - 1
    result = [0] * new_len
    for i in range(new_len):
        s = 0
        for j in range(i + 1):
            a = p[j] if j < len(p) else 0
            b_idx = i - j
            b = q[b_idx] if b_idx < len(q) else 0
            s = (s + a * b) % mod
        result[i] = s
    return result


def div_poly(p, q):
    result = [0] * 1000
    inv_q0 = pow(q[0], mod - 2, mod)
    for i in range(1000):
        acc = sum((q[j] * result[i - j]) % mod if j < len(q) and i - j >= 0 else 0 for j in range(1, i + 1))
        numerator = (p[i] - acc) % mod if i < len(p) else (-acc) % mod
        result[i] = (numerator * inv_q0) % mod
    return result



def print_poly(poly):
    print(len(poly) - 1)
    print(' '.join(map(str, poly)))


n, m = map(int, input().split())
p_coeffs = list(map(int, input().split()))
q_coeffs = list(map(int, input().split()))

sum_pq = add_poly(p_coeffs, q_coeffs)
print_poly(sum_pq)

prod_pq = mul_poly(p_coeffs, q_coeffs)
print_poly(prod_pq)

div_pq = div_poly(p_coeffs, q_coeffs)

print(' '.join(str(div_pq[i] if i < len(div_pq) else 0) for i in range(1000)))