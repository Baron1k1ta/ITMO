{-# LANGUAGE BlockArguments #-}
{-# LANGUAGE TypeApplications #-}

module Main (main) where

import Control.Exception (IOException, SomeException, catch, try)
import Control.Monad.IO.Class (liftIO)
import Data.Set (Set, fromList)
import Prettyprinter (defaultLayoutOptions, layoutPretty, line, (<+>))
import Prettyprinter.Render.Terminal (putDoc, renderIO)
import System.Console.Haskeline (InputT, defaultSettings, getExternalPrint, getInputLine, runInputT)
import System.IO (stdout)
import Text.Megaparsec (errorBundlePretty)

import HW5.Action (HIO(runHIO), HiPermission(..))
import HW5.Base (HiExpr, HiError, HiValue)
import HW5.Evaluator (eval)
import HW5.Parser (parse)
import HW5.Pretty (errorStyle, prettyValue)

replPermissions :: Set HiPermission
replPermissions = fromList [AllowRead, AllowWrite, AllowTime]

main :: IO ()
main = runInputT defaultSettings loop

loop :: InputT IO ()
loop = do
  minput <- getInputLine "hi> "
  case minput of
    Nothing      -> return ()
    Just ":q"    -> return ()
    Just ":quit" -> return ()
    Just ""      -> loop
    Just input   -> processInput input >> loop

processInput :: String -> InputT IO ()
processInput input = do
  output <- getExternalPrint
  case parse input of
    Left e       -> liftIO $ output (errorBundlePretty e)
    Right parsed -> evaluateAndPrint parsed output

evaluateAndPrint :: HiExpr -> (String -> IO ()) -> InputT IO ()
evaluateAndPrint expr output = do
  let computation = eval expr :: HIO (Either HiError HiValue)

  withHandling <- liftIO $ try @SomeException (runHIO computation replPermissions)
  case withHandling of
    Left ex -> liftIO $ output ("Error: " ++ show ex)
    Right evaluated -> do
      let doc =
            (<> line) $
              either
                (\e -> errorStyle "Error:" <+> errorStyle (show e))
                prettyValue
                evaluated

      liftIO $ catch
        (renderIO stdout (layoutPretty defaultLayoutOptions doc))
        (\ioe -> do
            putDoc (errorStyle "Error:" <+> errorStyle ("IO error: " ++ show (ioe :: IOException)))
            putStrLn "")

      liftIO (putStrLn "")