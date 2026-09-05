module HW3.T3
  ( joinOption,
    joinExcept,
    joinAnnotated,
    joinList,
    joinFun,
  )
where

import HW3.T1
  ( Annotated (..),
    Except (..),
    Fun (..),
    List (..),
    Option (..),
  )

joinOption :: Option (Option a) -> Option a
joinOption (Some (Some x)) = Some x
joinOption _ = None

joinExcept :: Except e (Except e a) -> Except e a
joinExcept (Error e) = Error e
joinExcept (Success a) = a

joinAnnotated :: (Semigroup e) => Annotated e (Annotated e a) -> Annotated e a
joinAnnotated ((x :# y) :# z) = x :# (z <> y)

joinList :: List (List a) -> List a
joinList Nil = Nil
joinList (x1 :. y1) = helper x1
  where
    helper (x2 :. y2) = x2 :. helper y2
    helper Nil = joinList y1

joinFun :: Fun i (Fun i a) -> Fun i a
joinFun (F f) = F (\i -> let (F g) = f i in g i)
