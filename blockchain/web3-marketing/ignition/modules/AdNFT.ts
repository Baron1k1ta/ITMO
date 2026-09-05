import { buildModule } from "@nomicfoundation/hardhat-ignition/modules";

export default buildModule("AdNFTModule", (m) => {
  // Deploy the advertising NFT contract; admin = deployer.
  const adNft = m.contract("Web3MarketingAdNFT");

  return { adNft };
});
