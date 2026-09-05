module HW2.T2
  ( splitOn
  , joinWith
  )
where

import Data.List.NonEmpty (NonEmpty (..))

splitOn :: (Eq a) => a -> [a] -> NonEmpty [a]
splitOn sep xs = createNE (foldr index [[]] xs)
  where
    index letter (current : rest)
      | letter == sep = [] : current : rest
      | otherwise = (letter : current) : rest
    index letter []
      | letter == sep = [[]]
      | otherwise     = [[letter]]
    createNE (h : t) = h :| t
    createNE []      = [] :| []

joinWith :: a -> NonEmpty [a] -> [a]
joinWith sep (h :| t) = h ++ concatMap (sep :) t
