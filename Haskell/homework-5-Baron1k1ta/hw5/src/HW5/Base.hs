{-# LANGUAGE DeriveGeneric #-}

module HW5.Base
  (HiError(..)
  , HiExpr(..)
  , HiFun(..)
  , HiValue(..)
  , HiAction(..)
  , hiFunMap
  , HiMonad(..)
  , HiShow(..)
  ) where

import           Codec.Serialise (Serialise)
import           Data.ByteString (ByteString)
import           Data.Map        (Map, fromList)
import           Data.Sequence   (Seq)
import           Data.Text       (Text)
import           Data.Time       (UTCTime)
import           GHC.Generics    (Generic)

data HiAction
  = HiActionRead  FilePath
  | HiActionWrite FilePath ByteString
  | HiActionMkDir FilePath
  | HiActionChDir FilePath
  | HiActionCwd
  | HiActionNow
  | HiActionRand Int Int
  | HiActionEcho Text
  deriving (Eq, Ord, Show, Generic)

class Monad m => HiMonad m where
  runAction :: HiAction -> m HiValue

data HiFun
  = HiFunDiv
  | HiFunMul
  | HiFunAdd
  | HiFunSub

  | HiFunNot
  | HiFunAnd
  | HiFunOr

  | HiFunLessThan
  | HiFunGreaterThan
  | HiFunEquals
  | HiFunNotLessThan
  | HiFunNotGreaterThan
  | HiFunNotEquals

  | HiFunIf

  | HiFunLength
  | HiFunToUpper
  | HiFunToLower
  | HiFunReverse
  | HiFunTrim

  | HiFunList
  | HiFunRange
  | HiFunFold

  | HiFunPackBytes
  | HiFunUnpackBytes
  | HiFunEncodeUtf8
  | HiFunDecodeUtf8
  | HiFunZip
  | HiFunUnzip
  | HiFunSerialise
  | HiFunDeserialise

  | HiFunRead
  | HiFunWrite
  | HiFunMkDir
  | HiFunChDir
  | HiFunParseTime
  | HiFunRand
  | HiFunEcho

  | HiFunCount
  | HiFunKeys
  | HiFunValues
  | HiFunInvert
  deriving (Enum, Bounded, Eq, Ord, Generic)

data HiValue
  = HiValueBool Bool
  | HiValueNumber Rational
  | HiValueFunction HiFun
  | HiValueNull
  | HiValueString Text
  | HiValueList (Seq HiValue)
  | HiValueBytes ByteString
  | HiValueAction HiAction
  | HiValueTime UTCTime
  | HiValueDict (Map HiValue HiValue)
  deriving (Eq, Ord, Show, Generic)

data HiExpr
  = HiExprValue HiValue
  | HiExprApply HiExpr [HiExpr]
  | HiExprRun HiExpr
  | HiExprDict [(HiExpr, HiExpr)]
  deriving (Eq, Ord, Show, Generic)


data HiError
  = HiErrorInvalidArgument
  | HiErrorInvalidFunction
  | HiErrorArityMismatch
  | HiErrorDivideByZero
  deriving (Eq, Ord, Show)

instance Serialise HiValue
instance Serialise HiFun
instance Serialise HiAction
instance Serialise HiExpr

class HiShow a where
  hiShow :: a -> String

instance HiShow HiFun where
  hiShow = show

instance HiShow HiAction where
  hiShow (HiActionRead _)    = "read"
  hiShow (HiActionWrite _ _) = "write"
  hiShow (HiActionMkDir _)   = "mkdir"
  hiShow (HiActionChDir _)   = "cd"
  hiShow HiActionCwd         = "cwd"
  hiShow HiActionNow         = "now"
  hiShow (HiActionRand _ _)  = "rand"
  hiShow (HiActionEcho _)    = "echo"

-- | `Show HiFun` with exact textual names (как в 3-м).
instance Show HiFun where
  show HiFunDiv            = "div"
  show HiFunMul            = "mul"
  show HiFunAdd            = "add"
  show HiFunSub            = "sub"

  show HiFunNot            = "not"
  show HiFunAnd            = "and"
  show HiFunOr             = "or"

  show HiFunLessThan       = "less-than"
  show HiFunGreaterThan    = "greater-than"
  show HiFunEquals         = "equals"
  show HiFunNotLessThan    = "not-less-than"
  show HiFunNotGreaterThan = "not-greater-than"
  show HiFunNotEquals      = "not-equals"

  show HiFunIf             = "if"

  show HiFunLength         = "length"
  show HiFunToUpper        = "to-upper"
  show HiFunToLower        = "to-lower"
  show HiFunReverse        = "reverse"
  show HiFunTrim           = "trim"

  show HiFunList           = "list"
  show HiFunRange          = "range"
  show HiFunFold           = "fold"

  show HiFunPackBytes      = "pack-bytes"
  show HiFunUnpackBytes    = "unpack-bytes"
  show HiFunEncodeUtf8     = "encode-utf8"
  show HiFunDecodeUtf8     = "decode-utf8"
  show HiFunZip            = "zip"
  show HiFunUnzip          = "unzip"
  show HiFunSerialise      = "serialise"
  show HiFunDeserialise    = "deserialise"

  show HiFunRead           = "read"
  show HiFunWrite          = "write"
  show HiFunMkDir          = "mkdir"
  show HiFunChDir          = "cd"
  show HiFunParseTime      = "parse-time"
  show HiFunRand           = "rand"
  show HiFunEcho           = "echo"

  show HiFunCount          = "count"
  show HiFunKeys           = "keys"
  show HiFunValues         = "values"
  show HiFunInvert         = "invert"

-- | Map from function constructor to its textual name (из 2-го).
hiFunMap :: Map HiFun String
hiFunMap =
  fromList
    [ (HiFunDiv, "div")
    , (HiFunMul, "mul")
    , (HiFunAdd, "add")
    , (HiFunSub, "sub")

    , (HiFunNot, "not")
    , (HiFunAnd, "and")
    , (HiFunOr, "or")

    , (HiFunLessThan, "less-than")
    , (HiFunGreaterThan, "greater-than")
    , (HiFunEquals, "equals")
    , (HiFunNotLessThan, "not-less-than")
    , (HiFunNotGreaterThan, "not-greater-than")
    , (HiFunNotEquals, "not-equals")

    , (HiFunIf, "if")

    , (HiFunLength, "length")
    , (HiFunToUpper, "to-upper")
    , (HiFunToLower, "to-lower")
    , (HiFunReverse, "reverse")
    , (HiFunTrim, "trim")

    , (HiFunList, "list")
    , (HiFunRange, "range")
    , (HiFunFold, "fold")

    , (HiFunPackBytes, "pack-bytes")
    , (HiFunUnpackBytes, "unpack-bytes")
    , (HiFunEncodeUtf8, "encode-utf8")
    , (HiFunDecodeUtf8, "decode-utf8")
    , (HiFunZip, "zip")
    , (HiFunUnzip, "unzip")
    , (HiFunSerialise, "serialise")
    , (HiFunDeserialise, "deserialise")

    , (HiFunRead, "read")
    , (HiFunWrite, "write")
    , (HiFunMkDir, "mkdir")
    , (HiFunChDir, "cd")
    , (HiFunParseTime, "parse-time")
    , (HiFunRand, "rand")
    , (HiFunEcho, "echo")

    , (HiFunCount, "count")
    , (HiFunKeys, "keys")
    , (HiFunValues, "values")
    , (HiFunInvert, "invert")
    ]
