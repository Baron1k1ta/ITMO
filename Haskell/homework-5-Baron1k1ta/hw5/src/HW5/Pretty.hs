{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE OverloadedStrings #-}

module HW5.Pretty
  ( prettyValue
  , errorStyle
  ) where


import Data.Foldable (toList, fold)
import Data.List (intersperse)
import Data.Ratio (denominator, numerator)
import Data.Scientific (formatScientific, FPFormat(Fixed), fromRationalRepetendUnlimited)
import Data.Time.Clock (UTCTime)
import Data.Word (Word8)
import Numeric (showFFloat)
import Prettyprinter (Doc, Pretty(pretty), annotate, concatWith, encloseSep, (<+>), viaShow)
import Prettyprinter.Render.Terminal (AnsiStyle, Color(..), color, bgColorDull, bold, italicized)

import HW5.Base

import qualified Data.ByteString as BS
import qualified Data.Map as Map
import qualified Data.Sequence as Seq


prettyValue :: HiValue -> Doc AnsiStyle
prettyValue = \case
  HiValueNull       -> nullStyle ("null" :: String)
  HiValueBool b     -> boolStyle (if b then ("true" :: String) else ("false" :: String))
  HiValueNumber r   -> prettifyNumber r
  HiValueString s   -> stringStyle (show s)
  HiValueList l     -> prettifyList l
  HiValueBytes b    -> prettyBytes b
  HiValueDict d     -> prettifyDict d
  HiValueFunction f -> prettifyFunction f
  HiValueAction a   -> prettyAction a
  HiValueTime t     -> funStyle ("parse-time" :: String) <> surroundBrackets ("(" :: String) (")" :: String) ("" :: String) [prettifyTime t]


prettifyFunction :: HiFun -> Doc AnsiStyle
prettifyFunction = funStyle . (hiFunMap Map.!)

prettifyTime :: UTCTime -> Doc AnsiStyle
prettifyTime t = timeStyle ("\"" ++ show t ++ "\"")

prettyAction :: HiAction -> Doc AnsiStyle
prettyAction action =
  case action of
    HiActionCwd          -> funStyle ("cwd" :: String)
    HiActionNow          -> funStyle ("now" :: String)
    HiActionRead p       -> prettyActionWithArgs ("read" :: String)   [stringStyle (show p)]
    HiActionWrite p b    -> prettyActionWithArgs ("write" :: String)  [stringStyle (show p), prettyBytes b]
    HiActionMkDir p      -> prettyActionWithArgs ("mkdir" :: String)  [stringStyle (show p)]
    HiActionChDir p      -> prettyActionWithArgs ("cd" :: String)     [stringStyle (show p)]
    HiActionRand l r     -> prettyActionWithArgs ("rand" :: String)   [numberStyle l, numberStyle r]
    HiActionEcho txt     -> prettyActionWithArgs ("echo" :: String)   [viaShow txt]

prettyActionWithArgs :: String -> [Doc AnsiStyle] -> Doc AnsiStyle
prettyActionWithArgs name args = funStyle name <> surroundBrackets ("(" :: String) (")" :: String) (", " :: String) args


prettifyDict :: Map.Map HiValue HiValue -> Doc AnsiStyle
prettifyDict d =
  surroundBrackets ("{ " :: String) (" }" :: String) (", " :: String) (Prelude.map prettifyMapEntry (Map.toList d))

prettifyMapEntry :: (HiValue, HiValue) -> Doc AnsiStyle
prettifyMapEntry (k, v) = prettyValue k <> pretty (":" :: String) <+> prettyValue v

prettifyList :: Seq.Seq HiValue -> Doc AnsiStyle
prettifyList s =
  if null (toList s)
    then bracketStyle ("[ ]" :: String)
    else encloseSep (bracketStyle ("[ " :: String)) (bracketStyle (" ]" :: String)) (pretty (", " :: String)) (Prelude.map prettyValue (toList s))

prettyBytes :: BS.ByteString -> Doc AnsiStyle
prettyBytes b =
  prettyJoin ("[#" :: String) ("#]" :: String) (" " :: String) (Prelude.map (pretty . word8ToHexPair) (BS.unpack b))
  where
    word8ToHexPair :: Word8 -> String
    word8ToHexPair w = [hexDigit (w `div` 16), hexDigit (w `mod` 16)]

    hexDigit :: Word8 -> Char
    hexDigit n
      | n < 10    = toEnum (fromEnum '0' + fromIntegral n)
      | otherwise = toEnum (fromEnum 'a' + fromIntegral (n - 10))

prettifyNumber :: Rational -> Doc AnsiStyle
prettifyNumber n
  | denominator n == 1 = numberStyle (numerator n)
  | otherwise =
      case fromRationalRepetendUnlimited n of
        (sci, Nothing) ->
          numberStyle (formatScientific Fixed Nothing sci)
            <|> numberStyle (showFFloat Nothing (fromRational n :: Double) "")
        _ ->
          let (q, r) = quotRem (numerator n) (denominator n)
           in if q == 0
                then prettyFraction r (denominator n)
                else numberStyle q <+> pretty (if r < 0 then ("-" :: String) else ("+" :: String)) <+> prettyFraction (abs r) (denominator n)

prettyFraction :: Integer -> Integer -> Doc AnsiStyle
prettyFraction a b = numberStyle a <> slash <> numberStyle b
  where
    slash = pretty ("/" :: String)


prettyJoin :: String -> String -> String -> [Doc AnsiStyle] -> Doc AnsiStyle
prettyJoin start end sep items =
  pretty start <+> concatWith (\x y -> x <> pretty sep <> y) items <+> pretty end

surroundBrackets :: String -> String -> String -> [Doc AnsiStyle] -> Doc AnsiStyle
surroundBrackets left right sep xs =
  if null xs
    then bracketStyle left <> pretty (" " :: String) <> bracketStyle right
    else bracketStyle left <> prettyConcat sep xs <> bracketStyle right

prettyConcat :: String -> [Doc AnsiStyle] -> Doc AnsiStyle
prettyConcat joiner = intercalateDoc (pretty joiner)

intercalateDoc :: Doc AnsiStyle -> [Doc AnsiStyle] -> Doc AnsiStyle
intercalateDoc e xs = mconcat (intersperse e xs)


stringStyle, numberStyle, boolStyle, nullStyle, timeStyle, funStyle, bracketStyle, errorStyle ::
     (Pretty t) => t -> Doc AnsiStyle
stringStyle = prettify (applyStyleSheet [color Green, italicized])
numberStyle = prettify (applyColor Blue)
boolStyle   = prettify (applyStyleSheet [color Cyan, bold])
nullStyle   = prettify (applyStyleSheet [color Yellow, bold, italicized, bgColorDull Magenta])
timeStyle   = prettify (applyColor Blue)
funStyle    = prettify (applyColor Magenta)
bracketStyle = prettify (applyColor Yellow)
errorStyle  = prettify (applyColor Red)

applyColor :: Color -> Doc AnsiStyle -> Doc AnsiStyle
applyColor col = annotate (color col)

applyStyleSheet :: [AnsiStyle] -> Doc AnsiStyle -> Doc AnsiStyle
applyStyleSheet styleSheet = annotate (fold styleSheet)

prettify :: (Pretty t) => (Doc AnsiStyle -> Doc AnsiStyle) -> t -> Doc AnsiStyle
prettify a = a . pretty

(<|>) :: Doc AnsiStyle -> Doc AnsiStyle -> Doc AnsiStyle
(<|>) = const