module HW1.T2
  ( N (..),
    nplus,
    nmult,
    nsub,
    nFromNatural,
    nToNum,
    ncmp,
    nEven,
    nOdd,
    ndiv,
    nmod,
  )
where

import Numeric.Natural

data N = Z | S N

nplus :: N -> N -> N
nplus Z y     = y
nplus (S x) y = nplus x (S y)

nmult :: N -> N -> N
nmult Z _     = Z
nmult (S x) y = nplus y (nmult x y)

nsub :: N -> N -> Maybe N
nsub x Z         = Just x
nsub Z (S _)     = Nothing
nsub (S x) (S y) = nsub x y

ncmp :: N -> N -> Ordering
ncmp Z Z         = EQ
ncmp (S _) Z     = GT
ncmp Z (S _)     = LT
ncmp (S x) (S y) = ncmp x y

-- nFromNatural :: Natural -> N
-- nFromNatural 0 = Z
-- nFromNatural x = S (nFromNatural (x - 1))

nFromNatural :: Natural -> N
nFromNatural n = accumulationFunc n Z
  where
    accumulationFunc 0 acc = acc
    accumulationFunc k acc = accumulationFunc (k - 1) (S acc)

nToNum :: (Num a) => N -> a
nToNum Z     = 0
nToNum (S x) = 1 + nToNum x

nEven :: N -> Bool
nEven Z     = True
nEven (S x) = not (nEven x)

nOdd :: N -> Bool
nOdd Z     = False
nOdd (S x) = not (nOdd x)

ndiv :: N -> N -> N
ndiv _ Z = error "Division by zero"
ndiv x y =
  case nsub x y of
    Nothing  -> Z
    Just res -> S (ndiv res y)

nmod :: N -> N -> N
nmod _ Z = error "Division by zero"
nmod x y =
  case nsub x y of
    Nothing  -> x
    Just res -> nmod res y
