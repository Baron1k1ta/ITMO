#include <iostream>
#include <vector>
#include <algorithm>

std::vector<int> char_indices[26];

int update_indices(int char_index, int left_bound, int right_bound, int start_value) {
    if (char_indices[char_index].empty()) {
        return start_value;
    }
    int left_pos = std::lower_bound(char_indices[char_index].begin(), char_indices[char_index].end(), left_bound) - char_indices[char_index].begin();
    int right_pos = std::upper_bound(char_indices[char_index].begin(), char_indices[char_index].end(), right_bound) - char_indices[char_index].begin();
    for (int i = left_pos; i < right_pos; i++) {
        char_indices[char_index][i] = start_value;
        start_value++;
    }
    return start_value;
}

int main() {
    int string_length, queries_count;
    std::cin >> string_length >> queries_count;
    std::string input_string;
    std::cin >> input_string;

    for (int i = 0; i < input_string.length(); i++) {
        int char_code = input_string[i] - 'a';
        char_indices[char_code].push_back(i);
    }

    for (int i = 0; i < queries_count; i++) {
        int left_bound, right_bound, order;
        std::cin >> left_bound >> right_bound >> order;
        left_bound--;
        right_bound--;
        int current_position = left_bound;

        if (order == 1) {
            for (int char_index = 0; char_index < 26; char_index++) {
                current_position = update_indices(char_index, left_bound, right_bound, current_position);
            }
        } else {
            for (int char_index = 25; char_index >= 0; char_index--) {
                current_position = update_indices(char_index, left_bound, right_bound, current_position);
            }
        }
    }

    for (int char_index = 0; char_index < 26; char_index++) {
        for (int j = 0; j < char_indices[char_index].size(); j++) {
            input_string[char_indices[char_index][j]] = 'a' + char_index;
        }
    }

    std::cout << input_string;
    return 0;
}