module HW2.T1
  ( Tree (..),
    tfoldr,
    treeToList,
  )
where

data Tree a
  = Leaf
  | Branch Int (Tree a) a (Tree a)
  deriving (Show)

tfoldr :: (a -> b -> b) -> b -> Tree a -> b
tfoldr _ acc Leaf = acc
tfoldr f acc (Branch _ l x r) =
  let accR = tfoldr f acc r
      accX = f x accR
   in tfoldr f accX l

treeToList :: Tree a -> [a]
treeToList = tfoldr (:) []
