#include <iostream>
#include <vector>
using namespace std;

const long long mod = 104857601;

long long getValueAtIndex(int index, const vector<long long>& array) {
    if (index >= array.size()) {
        return 0;
    }
    return array[index];
}

vector<long long> multiplyPolynomials(const vector<long long>& poly1, const vector<long long>& poly2) {
    int len1 = poly1.size();
    int len2 = poly2.size();
    int resultSize = len1 + len2 + 3;
    vector<long long> result(resultSize, 0);

    for (int i = 0; i < resultSize; i += 2) {
        for (int j = 0; j <= i; ++j) {
            result[i] = (result[i] + (getValueAtIndex(j, poly1) * getValueAtIndex(i - j, poly2)) % mod) % mod;
        }
    }
    return result;
}

int main() {
    long long k, n;
    cin >> k >> n;
    --n;

    vector<long long> p(2 * k, 0);
    vector<long long> q(k + 1, 0);

    for (int i = 0; i < k; ++i) {
        cin >> p[i];
    }

    q[0] = 1;
    long long coeff;
    for (int i = 1; i <= k; i++) {
        cin >> coeff;
        q[i] = (-coeff + mod) % mod;
    }

    while (n >= k) {
        for (int i = k; i < 2 * k; ++i) {
            p[i] = 0;
            for (int j = 1; j <= k; ++j) {
                p[i] = (p[i] - (q[j] * p[i - j]) % mod + mod) % mod;
            }
        }

        vector<long long> modifiedQ(k + 1, 0);
        for (int i = 0; i <= k; i += 2) {
            modifiedQ[i] = q[i];
        }

        for (int i = 1; i <= k; i += 2) {
            modifiedQ[i] = (-q[i] + mod) % mod;
        }

        vector<long long> product = multiplyPolynomials(q, modifiedQ);
        for (int i = 0; i <= k; ++i) {
            q[i] = product[i * 2];
        }

        for (int i = n % 2; i < 2 * k; i += 2) {
            p[i / 2] = p[i];
        }

        n /= 2;
    }

    cout << p[n] << endl;

    return 0;
}