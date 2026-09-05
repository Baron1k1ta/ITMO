module HW4.T2
  ( ParseError (..),
    Parser (..),
    runP,
    pChar,
    parseError,
    pEof,
    parseExpr,
  )
where

import Control.Applicative (Alternative (..), many, optional, some)
import Control.Monad (MonadPlus (..), void)
import Data.Char (isDigit, isSpace)
import HW4.T1 (ExceptState (..))
import HW4.Types
import Numeric.Natural (Natural)

data ParseError = ErrorAtPos Natural
  deriving (Eq, Show)

newtype Parser a = P (ExceptState ParseError (Natural, String) a)

runP :: Parser a -> String -> Except ParseError a
runP (P (ES run)) input =
  case run (0, input) of
    Error e -> Error e
    Success (a :# _) -> Success a

instance Functor Parser where
  fmap f (P p) = P (fmap f p)

instance Applicative Parser where
  pure x = P (pure x)
  (P pf) <*> (P pa) = P (pf <*> pa)

instance Monad Parser where
  (P p) >>= f = P (p >>= (\x -> let P q = f x in q))

pChar :: Parser Char
pChar = P $ ES $ \(pos, s) ->
  case s of
    [] -> Error (ErrorAtPos pos)
    (c : cs) -> Success (c :# (pos + 1, cs))

parseError :: Parser a
parseError = P $ ES $ \(pos, _) -> Error (ErrorAtPos pos)

instance Alternative Parser where
  empty = parseError
  (<|>) (P (ES p)) (P (ES q)) = P $ ES $ \st ->
    case p st of
      Success r -> Success r
      Error (ErrorAtPos e1) ->
        case q st of
          Success r -> Success r
          Error (ErrorAtPos e2) -> Error (ErrorAtPos (max e1 e2))

instance MonadPlus Parser

pEof :: Parser ()
pEof = P $ ES $ \(pos, s) ->
  case s of
    [] -> Success (() :# (pos, s))
    _ -> Error (ErrorAtPos pos)

pSatisfy :: (Char -> Bool) -> Parser Char
pSatisfy pr = P $ ES $ \(pos, s) ->
  case s of
    [] -> Error (ErrorAtPos pos)
    (c : cs)
      | pr c -> Success (c :# (pos + 1, cs))
      | otherwise -> Error (ErrorAtPos pos)

pSpaces :: Parser ()
pSpaces = void $ many (pSatisfy isSpace)

lexeme :: Parser a -> Parser a
lexeme p = p <* pSpaces

symbol :: Char -> Parser Char
symbol c = lexeme (pSatisfy (== c))

pDigits :: Parser String
pDigits = some (pSatisfy isDigit)

digitsToNat :: String -> Natural
digitsToNat = foldl (\acc d -> acc * 10 + fromIntegral (fromEnum d - fromEnum '0')) 0

pow10 :: Int -> Double
pow10 n = 10 ^^ n

pDouble :: Parser Double
pDouble = lexeme $ do
  intStr <- pDigits
  mFrac <- optional (symbol '.' *> pDigits)
  let intPart = fromIntegral (digitsToNat intStr) :: Double
  case mFrac of
    Nothing -> pure intPart
    Just fs ->
      let fracNat = digitsToNat fs
          len = length fs
          frac = fromIntegral fracNat / pow10 len
       in pure (intPart + frac)

parens :: Parser a -> Parser a
parens p = symbol '(' *> p <* symbol ')'

pFactor :: Parser Expr
pFactor =
  (Val <$> pDouble)
    <|> parens pExpr

pTerm :: Parser Expr
pTerm = do
  x <- pFactor
  rest x
  where
    rest acc =
      ( do
          _ <- symbol '*'
          y <- pFactor
          rest (Op (Mul acc y))
      )
        <|> ( do
                _ <- symbol '/'
                y <- pFactor
                rest (Op (Div acc y))
            )
        <|> pure acc

pExpr :: Parser Expr
pExpr = do
  x <- pTerm
  rest x
  where
    rest acc =
      ( do
          _ <- symbol '+'
          y <- pTerm
          rest (Op (Add acc y))
      )
        <|> ( do
                _ <- symbol '-'
                y <- pTerm
                rest (Op (Sub acc y))
            )
        <|> pure acc

parseExpr :: String -> Except ParseError Expr
parseExpr s =
  runP (pSpaces *> pExpr <* pEof) s
