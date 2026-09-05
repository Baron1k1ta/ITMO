module HW1.T3
  ( Tree (..),
    tsize,
    tdepth,
    tmember,
    tinsert,
    tFromList,
  )
where

-- (size, height)
type Meta = (Int, Int)

data Tree a = Leaf | Branch Meta (Tree a) a (Tree a)
  deriving (Show)

tsize :: Tree a -> Int
tsize Leaf = 0
tsize (Branch (sz, _) _ _ _) = sz

tdepth :: Tree a -> Int
tdepth Leaf = 0
tdepth (Branch (_, h) _ _ _) = h

tmember :: (Ord a) => a -> Tree a -> Bool
tmember _ Leaf = False
tmember x (Branch _ l y r) = case compare x y of
  LT -> tmember x l
  EQ -> True
  GT -> tmember x r

mkBranch :: Tree a -> a -> Tree a -> Tree a
mkBranch l x r = Branch meta l x r
  where
    sz = 1 + tsize l + tsize r
    h = 1 + max (tdepth l) (tdepth r)
    meta = (sz, h)

tinsert :: (Ord a) => a -> Tree a -> Tree a
tinsert x Leaf = mkBranch Leaf x Leaf
tinsert x tree@(Branch _ l y r) = case compare x y of
  LT -> balance (mkBranch (tinsert x l) y r)
  EQ -> tree
  GT -> balance (mkBranch l y (tinsert x r))

tFromList :: (Ord a) => [a] -> Tree a
tFromList = foldl (flip tinsert) Leaf

diff :: Tree a -> Int
diff Leaf = 0
diff (Branch _ l _ r) = tdepth l - tdepth r

rotateL :: Tree a -> Tree a
rotateL (Branch _ l x (Branch _ rl y rr)) =
  mkBranch (mkBranch l x rl) y rr
rotateL t = t

rotateR :: Tree a -> Tree a
rotateR (Branch _ (Branch _ ll y lr) x r) =
  mkBranch ll y (mkBranch lr x r)
rotateR t = t

balance :: Tree a -> Tree a
balance tree@(Branch _ l x r)
  | diff tree == 2 && diff l < 0 = rotateR (mkBranch (rotateL l) x r)
  | diff tree == 2 = rotateR tree
  | diff tree == -2 && diff r > 0 = rotateL (mkBranch l x (rotateR r))
  | diff tree == -2 = rotateL tree
  | otherwise = tree
balance Leaf = Leaf
