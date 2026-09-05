module HW4.T1
  ( ExceptState (..),
    mapExceptState,
    wrapExceptState,
    joinExceptState,
    modifyExceptState,
    throwExceptState,
    EvaluationError (..),
    eval,
  )
where

import HW4.Types

data ExceptState e s a = ES {runES :: s -> Except e (Annotated s a)}

mapExceptState :: (a -> b) -> ExceptState e s a -> ExceptState e s b
mapExceptState f (ES run) =
  ES $ \s ->
    case run s of
      Error e -> Error e
      Success (a :# s') -> Success (f a :# s')

wrapExceptState :: a -> ExceptState e s a
wrapExceptState a = ES $ \s -> Success (a :# s)

joinExceptState :: ExceptState e s (ExceptState e s a) -> ExceptState e s a
joinExceptState (ES run) =
  ES $ \s ->
    case run s of
      Error e -> Error e
      Success (m :# s') -> runES m s'

modifyExceptState :: (s -> s) -> ExceptState e s ()
modifyExceptState f = ES $ \s -> Success (() :# f s)

throwExceptState :: e -> ExceptState e s a
throwExceptState e = ES $ \_ -> Error e

instance Functor (ExceptState e s) where
  fmap = mapExceptState

instance Applicative (ExceptState e s) where
  pure = wrapExceptState
  (<*>) mf ma = do
    f <- mf
    a <- ma
    pure (f a)

instance Monad (ExceptState e s) where
  (>>=) m k = joinExceptState (fmap k m)

data EvaluationError = DivideByZero
  deriving (Eq, Show)

eval :: Expr -> ExceptState EvaluationError [Prim Double] Double
eval (Val x) = pure x
eval (Op op) =
  case op of
    Add a b -> binary Add (+) a b
    Sub a b -> binary Sub (-) a b
    Mul a b -> binary Mul (*) a b
    Div a b -> do
      x <- eval a
      y <- eval b
      if y == 0
        then throwExceptState DivideByZero
        else do
          modifyExceptState (Div x y :)
          pure (x / y)
    Abs a -> unary Abs abs a
    Sgn a -> unary Sgn signum a
  where
    binary ctor f a b = do
      x <- eval a
      y <- eval b
      modifyExceptState (ctor x y :)
      pure (f x y)

    unary ctor f a = do
      x <- eval a
      modifyExceptState (ctor x :)
      pure (f x)
