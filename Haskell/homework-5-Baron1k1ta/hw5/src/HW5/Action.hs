{-# LANGUAGE LambdaCase #-}
{-# LANGUAGE InstanceSigs #-}


module HW5.Action
  ( HIO(..)
  , HiPermission(..)
  , PermissionException(..)
  ) where

import HW5.Base

import Control.Monad (ap, void)
import Control.Exception (Exception, throwIO, throw)
import Data.Set (Set, member)

import Data.ByteString (ByteString)
import qualified Data.ByteString

import qualified Data.Sequence

import qualified Data.Text

import Data.Text.Encoding (decodeUtf8')

import System.Directory
  ( doesFileExist
  , listDirectory
  , createDirectory
  , setCurrentDirectory
  , getCurrentDirectory
  )

import System.Random (randomRIO)
import Data.Time (getCurrentTime)

import Control.Applicative (optional)



data HiPermission
  = AllowRead
  | AllowWrite
  | AllowTime
  deriving (Show, Enum, Ord, Eq, Bounded)

newtype PermissionException = PermissionRequired HiPermission
  deriving (Eq, Ord)

instance Show PermissionException where
  show (PermissionRequired p) =
    "Permission denied: " ++ show p ++ " is required."

instance Exception PermissionException

newtype HIO a = HIO { runHIO :: Set HiPermission -> IO a }


instance Functor HIO where
  fmap f (HIO runA) = HIO (\perms -> fmap f (runA perms))

instance Applicative HIO where
  pure a = HIO (\_ -> pure a)
  (<*>)  = ap

instance Monad HIO where
  (HIO runA) >>= f = HIO $ \perms -> do
    a <- runA perms
    runHIO (f a) perms

requirePermission :: HiPermission -> IO a
requirePermission = throwIO . PermissionRequired

requireAndAction :: HiPermission -> IO HiValue -> Set HiPermission -> IO HiValue
requireAndAction perm action perms =
  if perm `member` perms then action else requirePermission perm

needPermission :: HiPermission -> IO HiValue -> HIO HiValue
needPermission perm action = HIO $ \perms -> requireAndAction perm action perms

doWithPermission :: HIO HiValue -> HiPermission -> HIO HiValue
doWithPermission hio permType =
  HIO $ \perms ->
    if permType `member` perms
      then runHIO hio perms
      else throw (PermissionRequired permType)

instance HiMonad HIO where
  runAction :: HiAction -> HIO HiValue
  runAction = \case
    HiActionRead path ->
      doWithPermission (HIO $ \_ -> do
        isFile <- doesFileExist path
        if isFile
          then do
            bytes <- Data.ByteString.readFile path
            return (fromFile bytes)
          else do
            xs <- listDirectory path
            return (fromDir xs)
        ) AllowRead
      where
        fromFile :: ByteString -> HiValue
        fromFile bytes =
          case decodeUtf8' bytes of
            Left _  -> HiValueBytes bytes
            Right t -> HiValueString t

        fromDir :: [FilePath] -> HiValue
        fromDir =
          HiValueList
            . Data.Sequence.fromList
            . map (HiValueString . Data.Text.pack)

    HiActionWrite path bytes ->
      needPermission AllowWrite $ do
        Data.ByteString.writeFile path bytes
        pure HiValueNull

    HiActionMkDir path ->
      needPermission AllowWrite $ do
        void $ optional (createDirectory path)
        pure HiValueNull

    HiActionChDir path ->
      needPermission AllowRead $ do
        setCurrentDirectory path
        pure HiValueNull

    HiActionCwd ->
      needPermission AllowRead $ do
        dir <- getCurrentDirectory
        pure (HiValueString (Data.Text.pack dir))

    HiActionNow ->
      needPermission AllowTime $ HiValueTime <$> getCurrentTime

    HiActionRand lo hi ->
      HIO $ \_ -> HiValueNumber . toRational <$> randomRIO (lo, hi)

    HiActionEcho t ->
      needPermission AllowWrite $ do
        putStrLn (Data.Text.unpack t)
        pure HiValueNull

