{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE OverloadedStrings #-}

module HW5.Parser (parse) where

import Control.Monad (void)
import Control.Monad.Combinators (sepBy, sepBy1, sepEndBy, some, (<|>), count)
import Control.Monad.Combinators.Expr (Operator(..), makeExprParser)
import Data.Char (isAlpha, isAlphaNum)
import Data.List (intercalate)
import Data.Void (Void)
import Data.Word (Word8)
import Numeric (readHex)
import Text.Megaparsec (ParseErrorBundle, Parsec, between, choice, empty, many, manyTill, notFollowedBy, satisfy, try, runParser, eof)
import Text.Megaparsec.Char (char, hexDigitChar, space1, string)
import Text.Megaparsec.Char.Lexer (scientific, signed, space, lexeme, symbol, charLiteral)

import HW5.Base

import Data.ByteString (pack)
import Data.Text (pack)


type Parser = Parsec Void String

parse :: String -> Either (ParseErrorBundle String Void) HiExpr
parse = runParser (between skipSpaces eof parseHiExpr) ""


parseHiExpr :: Parser HiExpr
parseHiExpr = parseExpr

parseExpr :: Parser HiExpr
parseExpr = makeExprParser (ignoreSpaces term) tableBinaryOperators

term :: Parser HiExpr
term = (try getExpr <|> brackets parseExpr) >>= pApplication


tableBinaryOperators :: [[Operator Parser HiExpr]]
tableBinaryOperators =
  [ [ callChain ]

  , [ infixL "*" (withFun HiFunMul)
    , InfixL $ try $ withFun HiFunDiv <$ (string "/" <* notFollowedBy (string "=")) ]

  , [ infixL "+" (withFun HiFunAdd)
    , infixL "-" (withFun HiFunSub) ]

  , [ infixN "<=" (withFun HiFunNotGreaterThan)
    , infixN ">=" (withFun HiFunNotLessThan)
    , infixN "<"  (withFun HiFunLessThan)
    , infixN ">"  (withFun HiFunGreaterThan)
    , infixN "==" (withFun HiFunEquals)
    , infixN "/=" (withFun HiFunNotEquals) ]

  , [ infixR "&&" (withFun HiFunAnd) ]
  , [ infixR "||" (withFun HiFunOr) ]
  ]
  where
    withFun :: HiFun -> HiExpr -> HiExpr -> HiExpr
    withFun f a b = HiExprApply (HiExprValue $ HiValueFunction f) [a, b]

    infixL, infixR, infixN :: String -> (HiExpr -> HiExpr -> HiExpr) -> Operator Parser HiExpr
    infixL s f = InfixL $ try $ f <$ string s
    infixR s f = InfixR $ try $ f <$ string s
    infixN s f = InfixN $ try $ f <$ string s

    callChain :: Operator Parser HiExpr
    callChain = Postfix $ buildCallChain <$> parseSteps
      where
        parseSteps :: Parser [Either [HiExpr] ()]
        parseSteps = some $ choice
          [ Left <$> pArgs
          , Left <$> pAccessViaDot
          , Right () <$ makeSymbol "!"
          ]

        buildCallChain :: [Either [HiExpr] ()] -> (HiExpr -> HiExpr)
        buildCallChain = foldr applyStep id

        applyStep :: Either [HiExpr] () -> (HiExpr -> HiExpr) -> (HiExpr -> HiExpr)
        applyStep (Left args) next = \cur -> next (HiExprApply cur args)
        applyStep (Right _) next   = next . HiExprRun


pApplication :: HiExpr -> Parser HiExpr
pApplication f =
      (try (parseArgsOptional f) >>= pApplication)
  <|> (try (parseDotApply f)     >>= pApplication)
  <|> (try (pRun f)              >>= pApplication)
  <|> return f

parseArgsOptional :: HiExpr -> Parser HiExpr
parseArgsOptional expr = do
  argsLists <- many (inRoundBrackets (parseExpr `sepBy` comma))
  let applied = foldl HiExprApply expr argsLists
  return applied

parseDotApply :: HiExpr -> Parser HiExpr
parseDotApply expr = do
  _ <- string "."
  parts <- ((:) <$> satisfy isAlpha <*> many (satisfy isAlphaNum)) `sepBy1` char '-'
  let arg = HiExprValue (HiValueString (Data.Text.pack (intercalate "-" parts)))
  return (HiExprApply expr [arg])

pRun :: HiExpr -> Parser HiExpr
pRun f = HiExprRun f <$ char '!'

getExpr :: Parser HiExpr
getExpr =
  choice
    [ try pDict
    , try pList
    , try pListBytes
    , parseAsHiValue
    ]

parseAsHiValue :: Parser HiExpr
parseAsHiValue = HiExprValue <$> makeLexeme pValue

pValue :: Parser HiValue
pValue = choice
  [ try pNumber
  , try pBool
  , try pNull
  , try pString
  , try pBytes
  , try pFunction
  , pAction
  ]

pNumber :: Parser HiValue
pNumber = HiValueNumber . toRational
  <$> signed skipSpaces scientific

pBool :: Parser HiValue
pBool = choice
  [ HiValueBool True  <$ string "true"
  , HiValueBool False <$ string "false"
  ]

pNull :: Parser HiValue
pNull = HiValueNull <$ string "null"

pString :: Parser HiValue
pString =
  HiValueString . Data.Text.pack
    <$> (char '"' >> manyTill charLiteral (char '"'))

pAction :: Parser HiValue
pAction = HiValueAction <$> choice
  [ HiActionNow   <$ string "now"
  , HiActionCwd   <$ string "cwd"
  ]

pFunction :: Parser HiValue
pFunction = HiValueFunction <$> choice (map (\x -> x <$ string (hiShow x)) allFuncs)
  where
    allFuncs = [HiFunDiv .. HiFunEcho]

pBytes :: Parser HiValue
pBytes = do
  ws <- betweenSymbols "[#" "#]" (pByte `sepEndBy` space1)
  return $ HiValueBytes (Data.ByteString.pack ws)
  where
    pByte :: Parser Word8
    pByte = do
      chars <- count 2 hexDigitChar
      case readHex chars :: [(Int, String)] of
        [(v, "")] | v <= 255 -> return (fromIntegral v)
        _                    -> empty

pListBytes :: Parser HiExpr
pListBytes = HiExprValue <$> pBytes

pList :: Parser HiExpr
pList =
  HiExprApply (HiExprValue $ HiValueFunction HiFunList)
    <$> betweenSymbols "[" "]" (parseExpr `sepBy` comma)

pDict :: Parser HiExpr
pDict = HiExprDict <$> betweenSymbols "{" "}" (pEntry `sepBy` comma)
  where
    pEntry = (,) <$> (parseExpr <* colon) <*> parseExpr


pArgs :: Parser [HiExpr]
pArgs = inRoundBrackets (parseExpr `sepBy` comma)

pAccessViaDot :: Parser [HiExpr]
pAccessViaDot = do
  void $ char '.'
  parts <- ((:) <$> satisfy isAlpha <*> many (satisfy isAlphaNum)) `sepBy` char '-'
  return [HiExprValue $ HiValueString (Data.Text.pack $ intercalate "-" parts)]


betweenSymbols :: String -> String -> Parser a -> Parser a
betweenSymbols begin end = between (makeSymbol begin) (makeSymbol end)

brackets :: Parser a -> Parser a
brackets = betweenSymbols "(" ")"

inRoundBrackets :: Parser a -> Parser a
inRoundBrackets = brackets

comma, colon :: Parser String
comma = parseSymbol ","
colon = parseSymbol ":"

parseSymbol :: String -> Parser String
parseSymbol = symbol skipSpaces

makeSymbol :: String -> Parser String
makeSymbol = symbol skipSpaces

makeLexeme :: Parser a -> Parser a
makeLexeme = lexeme skipSpaces

ignoreSpaces :: Parser a -> Parser a
ignoreSpaces = between sc sc

sc :: Parser ()
sc = skipSpaces

skipSpaces :: Parser ()
skipSpaces = space space1 empty empty