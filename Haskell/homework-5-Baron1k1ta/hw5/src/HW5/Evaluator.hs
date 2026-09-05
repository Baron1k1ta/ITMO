{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE FlexibleInstances #-}
{-# LANGUAGE ScopedTypeVariables #-}



module HW5.Evaluator (eval) where

import Codec.Compression.Zlib (compressWith, decompress, CompressParams(..), defaultCompressParams, bestCompression)
import Codec.Serialise (serialise, deserialiseOrFail)
import Control.Monad (foldM)
import Control.Monad.Trans.Class (lift)
import Control.Monad.Trans.Except (ExceptT, runExceptT, throwE)
import Data.Bitraversable (bimapM)
import Data.ByteString ( ByteString, ByteString )
import Data.ByteString.Lazy (fromStrict, toStrict)
import Data.Either (fromRight)
import Data.Foldable (toList)
import Data.Map ( Map, Map )
import Data.Ratio (denominator, numerator)
import Data.Sequence ( Seq(..), (><), Seq(..), (><) )
import Data.Text.Encoding (decodeUtf8', encodeUtf8)
import Data.Time (addUTCTime, diffUTCTime)
import Data.Word (Word8)
import Text.Read (readMaybe)
import qualified Data.ByteString
import qualified Data.Map
import qualified Data.Sequence
import Data.Semigroup (stimes)
import Data.Text (Text)
import qualified Data.Text
import Data.Maybe (fromMaybe)
import HW5.Base


type HiExT m a = ExceptT HiError m a

eval :: HiMonad m => HiExpr -> m (Either HiError HiValue)
eval = runExceptT . evalExpression


evalExpression :: HiMonad m => HiExpr -> HiExT m HiValue
evalExpression = eval'  -- alias (из 1-го: eval')

eval' :: HiMonad m => HiExpr -> HiExT m HiValue
eval' = \case
  HiExprValue v -> return v

  HiExprDict pairs ->
    (HiValueDict . Data.Map.fromList <$> mapM (bimapM eval' eval') pairs)

  HiExprRun aExpr ->
    eval' aExpr >>= \case
      HiValueAction a -> lift (runAction a)
      _               -> throwE HiErrorInvalidArgument

  HiExprApply fExpr argsExprs -> evalApplicative fExpr argsExprs

evalApplicative :: HiMonad m => HiExpr -> [HiExpr] -> HiExT m HiValue
evalApplicative expr args = do
  f <- eval' expr
  case f of
    HiValueFunction fun -> (Data.Map.!) descriptionMap fun args
    HiValueString s     -> evalSliceable s HiValueString args
    HiValueList  l      -> evalSliceable l HiValueList args
    HiValueBytes b      -> evalSliceable b HiValueBytes args
    HiValueDict  d      -> evalDictGet d args
    _                   -> throwE HiErrorInvalidFunction

evalDictGet :: HiMonad m => Data.Map.Map HiValue HiValue -> [HiExpr] -> HiExT m HiValue
evalDictGet dict =
  \case
    [a] -> do
      key <- eval' a
      return (fromMaybe HiValueNull (Data.Map.lookup key dict))
    _   -> throwE HiErrorInvalidArgument


tryLazyEval :: HiMonad m => HiFun -> [HiExpr] -> HiExT m HiValue
tryLazyEval HiFunAnd [left, right] = do
  a1 <- eval' left
  case a1 of
    HiValueBool False -> return a1
    HiValueNull       -> return a1
    _                 -> eval' right

tryLazyEval HiFunOr [left, right] = do
  a1 <- eval' left
  case a1 of
    HiValueBool False -> eval' right
    HiValueNull       -> eval' right
    _                 -> return a1

tryLazyEval HiFunIf [cond, onTrue, onFalse] = do
  c <- eval' cond
  case c of
    HiValueBool True  -> eval' onTrue
    HiValueBool False -> eval' onFalse
    _                 -> throwE HiErrorInvalidArgument

tryLazyEval fun args = evalF fun =<< mapM eval' args


descriptionMap :: HiMonad m => Data.Map.Map HiFun ([HiExpr] -> HiExT m HiValue)
descriptionMap =
  Data.Map.fromList
    [ (HiFunDiv,          strictEval1 $ evalBinaryNonLazy (tryLazyEval HiFunDiv))
    , (HiFunMul,          strictEval1 $ evalBinaryNonLazy (tryLazyEval HiFunMul))
    , (HiFunAdd,          strictEval1 $ evalBinaryNonLazy (tryLazyEval HiFunAdd))
    , (HiFunSub,          strictEval1 $ evalBinaryNonLazy (tryLazyEval HiFunSub))
    , (HiFunNot,          strictEval1 $ evalUnaryNonLazy  (tryLazyEval HiFunNot))
    , (HiFunAnd,          tryLazyEval HiFunAnd)
    , (HiFunOr,           tryLazyEval HiFunOr)
    , (HiFunLessThan,     compareWith (<))
    , (HiFunGreaterThan,  compareWith (>))
    , (HiFunEquals,       compareWith (==))
    , (HiFunNotLessThan,  compareWith (>=))
    , (HiFunNotGreaterThan, compareWith (<=))
    , (HiFunNotEquals,    compareWith (/=))
    , (HiFunIf,           tryLazyEval HiFunIf)

    , (HiFunLength,       tryLazyEval HiFunLength)
    , (HiFunToUpper,      tryLazyEval HiFunToUpper)
    , (HiFunToLower,      tryLazyEval HiFunToLower)
    , (HiFunReverse,      tryLazyEval HiFunReverse)
    , (HiFunTrim,         tryLazyEval HiFunTrim)

    , (HiFunList, fmap (HiValueList . Data.Sequence.fromList) . mapM eval')
    , (HiFunRange,        tryLazyEval HiFunRange)
    , (HiFunFold,         tryLazyEval HiFunFold)

    , (HiFunPackBytes,    tryLazyEval HiFunPackBytes)
    , (HiFunUnpackBytes,  tryLazyEval HiFunUnpackBytes)
    , (HiFunEncodeUtf8,   tryLazyEval HiFunEncodeUtf8)
    , (HiFunDecodeUtf8,   tryLazyEval HiFunDecodeUtf8)
    , (HiFunZip,          tryLazyEval HiFunZip)
    , (HiFunUnzip,        tryLazyEval HiFunUnzip)
    , (HiFunSerialise,    tryLazyEval HiFunSerialise)
    , (HiFunDeserialise,  tryLazyEval HiFunDeserialise)

    , (HiFunRead,         tryLazyEval HiFunRead)
    , (HiFunWrite,        tryLazyEval HiFunWrite)
    , (HiFunMkDir,        tryLazyEval HiFunMkDir)
    , (HiFunChDir,        tryLazyEval HiFunChDir)
    , (HiFunParseTime,    tryLazyEval HiFunParseTime)
    , (HiFunRand,         tryLazyEval HiFunRand)
    , (HiFunEcho,         tryLazyEval HiFunEcho)

    , (HiFunCount,        tryLazyEval HiFunCount)
    , (HiFunKeys,         tryLazyEval HiFunKeys)
    , (HiFunValues,       tryLazyEval HiFunValues)
    , (HiFunInvert,       tryLazyEval HiFunInvert)
    ]


word8ToHiValue :: Word8 -> HiValue
word8ToHiValue w = HiValueNumber (toRational (fromIntegral w :: Integer))

evalF :: HiMonad m => HiFun -> [HiValue] -> HiExT m HiValue
evalF HiFunDiv [HiValueNumber a, HiValueNumber b] =
  if b == 0 then throwE HiErrorDivideByZero else return (HiValueNumber (a / b))
evalF HiFunDiv [HiValueString s1, HiValueString s2] =
  return (HiValueString (Data.Text.concat [s1, Data.Text.pack "/", s2]))

evalF HiFunMul [HiValueNumber a, HiValueNumber b] = return (HiValueNumber (a * b))
evalF HiFunMul [HiValueString t, HiValueNumber n] = checkedStimes n (return . HiValueString) t
evalF HiFunMul [HiValueList l,   HiValueNumber n] = checkedStimes n (return . HiValueList)   l
evalF HiFunMul [HiValueBytes b,  HiValueNumber n] = checkedStimes n (return . HiValueBytes)  b

evalF HiFunAdd [HiValueNumber a, HiValueNumber b] = return (HiValueNumber (a + b))
evalF HiFunAdd [HiValueString a, HiValueString b] = return (HiValueString (a <> b))
evalF HiFunAdd [HiValueList a,   HiValueList b]   = return (HiValueList (a >< b))
evalF HiFunAdd [HiValueBytes a,  HiValueBytes b]  = return (HiValueBytes (a <> b))
evalF HiFunAdd [HiValueTime t,   HiValueNumber n] =
  return (HiValueTime (addUTCTime (fromRational n) t))

evalF HiFunSub [HiValueNumber a, HiValueNumber b] = return (HiValueNumber (a - b))
evalF HiFunSub [HiValueTime t1, HiValueTime t2]   =
  return (HiValueNumber (toRational (diffUTCTime t1 t2)))

evalF HiFunNot [HiValueBool b] = return (HiValueBool (not b))

evalF HiFunToUpper [HiValueString t] = return (HiValueString (Data.Text.toUpper t))
evalF HiFunToLower [HiValueString t] = return (HiValueString (Data.Text.toLower t))
evalF HiFunTrim    [HiValueString t] = return (HiValueString (Data.Text.strip t))

evalF HiFunLength [HiValueString t] = return (HiValueNumber (toRational (Data.Text.length t)))
evalF HiFunLength [HiValueList l]   = return (HiValueNumber (toRational (Data.Sequence.length l)))
evalF HiFunLength [HiValueBytes b]  = return (HiValueNumber (toRational (Data.ByteString.length b)))

evalF HiFunReverse [HiValueString t] = return (HiValueString (Data.Text.reverse t))
evalF HiFunReverse [HiValueList l]   = return (HiValueList (Data.Sequence.reverse l))

evalF HiFunRange [HiValueNumber a, HiValueNumber b] =
  return (HiValueList (Data.Sequence.fromList (HiValueNumber <$> [a .. b])))

evalF HiFunFold [HiValueFunction f, HiValueList l] =
  case l of
    Empty      -> return HiValueNull
    (h :<| t)  ->
      foldM (\acc v -> tryLazyEval f [HiExprValue acc, HiExprValue v]) h (toList t)
evalF HiFunFold _ = throwE HiErrorInvalidArgument

evalF HiFunPackBytes [HiValueList xs] = do
  ws <- mapM getByte (toList xs)
  return (HiValueBytes (Data.ByteString.pack ws))
  where
    getByte :: HiMonad m => HiValue -> HiExT m Word8
    getByte = \case
      HiValueNumber n
        | denominator n == 1
        , let k = numerator n
        , 0 <= k && k <= 255 -> return (fromIntegral k)
      _ -> throwE HiErrorInvalidArgument

evalF HiFunUnpackBytes [HiValueBytes b] =
  return (HiValueList (Data.Sequence.fromList (word8ToHiValue <$> Data.ByteString.unpack b)))

evalF HiFunEncodeUtf8 [HiValueString t] =
  return (HiValueBytes (Data.Text.Encoding.encodeUtf8 t))

evalF HiFunDecodeUtf8 [HiValueBytes b] =
  return (either (const HiValueNull) HiValueString (Data.Text.Encoding.decodeUtf8' b))

evalF HiFunZip [HiValueBytes b] =
  return (HiValueBytes (Data.ByteString.Lazy.toStrict
          (compressWith defaultCompressParams { compressLevel = bestCompression }
           (Data.ByteString.Lazy.fromStrict b))))

evalF HiFunUnzip [HiValueBytes b] =
  return (HiValueBytes (Data.ByteString.Lazy.toStrict
          (decompress (Data.ByteString.Lazy.fromStrict b))))

evalF HiFunSerialise [x] =
  return (HiValueBytes (Data.ByteString.Lazy.toStrict (serialise x)))

evalF HiFunDeserialise [HiValueBytes b] =
  return (fromRight HiValueNull (deserialiseOrFail (Data.ByteString.Lazy.fromStrict b)))

evalF HiFunRead [HiValueString p] =
  return (HiValueAction (HiActionRead (Data.Text.unpack p)))

evalF HiFunWrite [HiValueString p, HiValueString t] =
  return (HiValueAction (HiActionWrite (Data.Text.unpack p) (Data.Text.Encoding.encodeUtf8 t)))

evalF HiFunMkDir [HiValueString p] =
  return (HiValueAction (HiActionMkDir (Data.Text.unpack p)))

evalF HiFunChDir [HiValueString p] =
  return (HiValueAction (HiActionChDir (Data.Text.unpack p)))

evalF HiFunParseTime [HiValueString s] =
  return (maybe HiValueNull HiValueTime (readMaybe (Data.Text.unpack s)))

evalF HiFunRand [HiValueNumber lo, HiValueNumber hi]
  | denominator lo == 1, denominator hi == 1 =
      return (HiValueAction (HiActionRand (fromInteger (numerator lo)) (fromInteger (numerator hi))))
  | otherwise = throwE HiErrorInvalidArgument

evalF HiFunEcho [HiValueString s] =
  return (HiValueAction (HiActionEcho s))

-- Dict ops
evalF HiFunKeys [HiValueDict d] =
  return (HiValueList (Data.Sequence.fromList (Data.Map.keys d)))

evalF HiFunValues [HiValueDict d] =
  return (HiValueList (Data.Sequence.fromList (Data.Map.elems d)))

evalF HiFunInvert [HiValueDict d] =
  return (HiValueDict (Data.Map.map (HiValueList . Data.Sequence.fromList)
           (Data.Map.fromListWith (++) [ (v, [k]) | (k,v) <- Data.Map.toList d ] )))

evalF HiFunCount [HiValueList l] =
  return (HiValueDict (countOccurrences id (toList l)))

evalF HiFunCount [HiValueBytes b] =
  return (HiValueDict (countOccurrences word8ToHiValue (Data.ByteString.unpack b)))

evalF HiFunCount [HiValueString t] =
  return (HiValueDict (countOccurrences (HiValueString . Data.Text.singleton) (Data.Text.unpack t)))

-- Fallback
evalF _ _ = throwE HiErrorInvalidArgument



strictEval1 :: HiMonad m => ([HiValue] -> HiExT m HiValue) -> [HiExpr] -> HiExT m HiValue
strictEval1 f args = do
  args' <- mapM eval' args
  f args'

evalUnaryNonLazy :: HiMonad m => ([HiExpr] -> HiExT m HiValue) -> [HiValue] -> HiExT m HiValue
evalUnaryNonLazy _ = \case
  [_] -> throwE HiErrorInvalidArgument
  _   -> throwE HiErrorInvalidArgument

evalBinaryNonLazy :: HiMonad m => ([HiExpr] -> HiExT m HiValue) -> [HiValue] -> HiExT m HiValue
evalBinaryNonLazy _ _ = throwE HiErrorInvalidArgument


compareWith :: HiMonad m => (HiValue -> HiValue -> Bool) -> [HiExpr] -> HiExT m HiValue
compareWith op = strictEval1 $ \case
  [a,b] -> return (HiValueBool (op a b))
  _     -> throwE HiErrorArityMismatch


class Sliceable a where
  lengthS      :: a -> Int
  indexS       :: Int -> a -> HiValue
  sliceS       :: Int -> Int -> a -> a

instance Sliceable Data.Text.Text where
  lengthS = Data.Text.length
  indexS i t = HiValueString (Data.Text.singleton (Data.Text.index t i))
  sliceS a b = Data.Text.take (b - a) . Data.Text.drop a

instance Sliceable (Seq HiValue) where
  lengthS = Data.Sequence.length
  indexS i s = Data.Sequence.index s i
  sliceS a b = Data.Sequence.take (b - a) . Data.Sequence.drop a

instance Sliceable ByteString where
  lengthS = Data.ByteString.length
  indexS i b = HiValueNumber (toRational (fromIntegral (Data.ByteString.index b i) :: Integer))
  sliceS a b = Data.ByteString.take (b - a) . Data.ByteString.drop a

evalSliceable :: (HiMonad m, Sliceable a) => a -> (a -> HiValue) -> [HiExpr] -> HiExT m HiValue
evalSliceable container constructor = \case
  [ex] -> do
    v <- eval' ex
    case v of
      HiValueNumber r
        | isInt r ->
            let i = fromInteger (numerator r)
                len = lengthS container
            in if i < 0 || i >= len
               then return HiValueNull
               else return (indexS i container)
      _ -> throwE HiErrorInvalidArgument

  [ex1, ex2] -> do
    v1 <- eval' ex1
    v2 <- eval' ex2
    let len = lengthS container
        norm :: HiMonad m => HiValue -> HiExT m Int
        norm = \case
          HiValueNull -> return 0
          HiValueNumber r
            | isInt r ->
                let i = fromInteger (numerator r)
                in return (if i < 0 then max 0 (len + i) else min len i)
          _ -> throwE HiErrorInvalidArgument
    a <- case v1 of
           HiValueNull -> return 0
           _           -> norm v1
    b <- case v2 of
           HiValueNull -> return len
           _           -> norm v2
    return (constructor (sliceS (min a b) (max a b) container))

  _ -> throwE HiErrorArityMismatch


isInt :: Rational -> Bool
isInt r = denominator r == 1

checkedStimes
  :: (HiMonad m, Semigroup s)
  => Rational
  -> (s -> HiExT m HiValue)
  -> s
  -> HiExT m HiValue
checkedStimes n wrap x =
  if isInt n && numerator n >= 0
    then wrap (stimes (numerator n) x)
    else throwE HiErrorInvalidArgument

countOccurrences :: (k -> HiValue) -> [k] -> Data.Map.Map HiValue HiValue
countOccurrences toHV xs =
  Data.Map.map (HiValueNumber . toRational)
    (Data.Map.fromListWith (+) [ (toHV x, 1 :: Int) | x <- xs ])