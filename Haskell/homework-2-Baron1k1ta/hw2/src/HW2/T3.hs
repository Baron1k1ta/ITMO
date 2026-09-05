module HW2.T3 (mcat, epart) where

extractValue :: (Monoid a) => Maybe a -> a
extractValue Nothing = mempty
extractValue (Just x) = x

mcat :: (Monoid a) => [Maybe a] -> a
mcat = foldMap extractValue

extractLeft :: (Monoid a) => Either a b -> a
extractLeft (Left a) = a
extractLeft (Right _) = mempty

extractRight :: (Monoid b) => Either a b -> b
extractRight (Left _) = mempty
extractRight (Right b) = b

epart :: (Monoid a, Monoid b) => [Either a b] -> (a, b)
epart list = (foldMap extractLeft list, foldMap extractRight list)
