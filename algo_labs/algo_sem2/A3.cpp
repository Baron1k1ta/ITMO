#include "avl.h"

#include <iostream>
size_t height(node *root) {
    return root ? root->h : 0;
}

void update_height(node *root) {
    if (root) {
        size_t left_height = root->l ? root->l->h : 0;
        size_t right_height = root->r ? root->r->h : 0;
        root->h = (left_height > right_height ? left_height : right_height) + 1;
    }
}

node* rotate_right(node *x) {
    node *y = x->l;
    x->l = y->r;
    y->r = x;
    update_height(x);
    update_height(y);
    return y;
}

node* rotate_left(node *x) {
    node *y = x->r;
    x->r = y->l;
    y->l = x;
    update_height(x);
    update_height(y);
    return y;
}
node* balance(node *root) {
    if (!root) {
        return nullptr;
    }

    update_height(root);

    int balance_factor = height(root->l) - height(root->r);

    if (balance_factor > 1) {
        if (height(root->l->r) > height(root->l->l)) {
            root->l = rotate_left(root->l);
        }
        return rotate_right(root);
    } else if (balance_factor < -1) {
        if (height(root->r->l) > height(root->r->r)) {
            root->r = rotate_right(root->r);
        }
        return rotate_left(root);
    }

    return root;
}


node* insert(node *root, int key) {
    if (!root) {
        root = new node{nullptr, nullptr, key, 1};
    } else if (key < root->key) {
        root->l = insert(root->l, key);
    } else if (key > root->key) {
        root->r = insert(root->r, key);
    }

    return balance(root);
}

node* find_min(node *root) {
    if (!root) {
        return nullptr;
    }
    while (root->l) {
        root = root->l;
    }
    return root;
}

node* remove(node *root, int key) {
    if (!root) {
        return nullptr;
    }

    if (key < root->key) {
        root->l = remove(root->l, key);
    } else if (key > root->key) {
        root->r = remove(root->r, key);
    } else {
        if (!root->l && !root->r) {
            delete root;
            return nullptr;
        } else if (!root->l || !root->r) {
            node *temp = root->l ? root->l : root->r;
            delete root;
            return temp;
        } else {
            node *min_right = find_min(root->r);
            root->key = min_right->key;
            root->r = remove(root->r, min_right->key);
        }
    }

    return balance(root);
}

bool exists(node *root, int key) {
    while (root) {
        if (key < root->key) {
            root = root->l;
        } else if (key > root->key) {
            root = root->r;
        } else {
            return true;
        }
    }
    return false;
}