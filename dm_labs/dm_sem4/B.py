mod = 998244353

def find_gcd(a, b):
    x, y = 0, 1
    if a == 0:
        return b, x, y
    g, x1, y1 = find_gcd(b % a, a)
    x = y1 - (b // a) * x1
    y = x1
    return g, x, y

def mod_inverse(el):
    g, x, _ = find_gcd(el, mod)
    if g != 1:
        return 1
    return (x % mod + mod) % mod

def ensure_non_negative(elem):
    return elem + mod if elem < 0 else elem


def compute_polynomial_degrees(poly_degrees, size):
    for k in range(2, len(poly_degrees)):
        for i in range(size + 1):
            mult = sum(
                (poly_degrees[1][j] if j < len(poly_degrees[1]) else 0) *
                (poly_degrees[k - 1][i - j] if i - j < len(poly_degrees[k - 1]) else 0)
                for j in range(i + 1)
            ) % mod
            poly_degrees[k].append(ensure_non_negative(mult))


def compute_sqrt(size):
    sqrt_vals = [0] * (size + 2)
    sqrt_vals[0] = 1
    for i in range(1, size + 1):
        sqrt_vals[i] = (((mod - sqrt_vals[i - 1]) % mod) * (((16 * i * i - 8 * i * i * i - 6 * i) + mod) % mod)) % mod
        sqrt_vals[i] = sqrt_vals[i] * mod_inverse(((i * i * 4 - 8 * i * i * i) + mod) % mod) % mod
        sqrt_vals[i] = ensure_non_negative(sqrt_vals[i])
    return sqrt_vals


def compute_exp(size):
    exp_vals = [0] * (size + 2)
    exp_vals[0] = 1
    for i in range(1, size + 1):
        exp_vals[i] = (exp_vals[i - 1] * mod_inverse(i)) % mod
        exp_vals[i] = ensure_non_negative(exp_vals[i])
    return exp_vals


def compute_log(size):
    log_vals = [0] * (size + 2)
    log_vals[0] = 0
    log_vals[1] = 1
    for i in range(2, size + 1):
        log_vals[i] = (((mod - log_vals[i - 1]) % mod) * (i - 1)) % mod
        log_vals[i] = (log_vals[i] * mod_inverse(i)) % mod
        log_vals[i] = ensure_non_negative(log_vals[i])
    return log_vals


def calculate_final_result(func_vals, poly_degrees, m):
    result = []
    for i in range(m):
        term = sum(
            (func_vals[j] * poly_degrees[j][i]) % mod
            for j in range(m)
        ) % mod
        result.append(ensure_non_negative(term))
    return result



n, m = map(int, input().split())
size = 100
poly_degrees = [[] for _ in range(size + 1)]
poly_degrees[0] = [0] * (size + 1)
poly_degrees[0][0] = 1
poly_degrees[1] = [0] * (size + 1)


poly_degrees[1][:n + 1] = list(map(int, input().split()))

compute_polynomial_degrees(poly_degrees, size)

sqrt_result = calculate_final_result(compute_sqrt(size), poly_degrees, m)
exp_result = calculate_final_result(compute_exp(size), poly_degrees, m)
log_result = calculate_final_result(compute_log(size), poly_degrees, m)

print(" ".join(map(str, sqrt_result)))
print(" ".join(map(str, exp_result)))
print(" ".join(map(str, log_result)))